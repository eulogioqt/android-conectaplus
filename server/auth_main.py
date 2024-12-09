import asyncio
import jwt
import datetime
from websockets import serve, exceptions
from aiohttp import web

SECRET_KEY = "mi_secreto_super_seguro"
connected_clients = set()

async def handler(websocket, path):
    # Extraer y validar el token del encabezado Authorization
    auth_header = websocket.request_headers.get('Authorization')
    if not auth_header or not auth_header.startswith('Bearer '):
        await websocket.close(code=1011, reason="No autorizado: falta token")
        return

    token = auth_header.split(' ')[1]
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=["HS256"])
        user_id = payload.get("sub", "desconocido")
        print(f"Usuario conectado: {user_id}")
    except jwt.ExpiredSignatureError:
        await websocket.close(code=1011, reason="Token expirado")
        return
    except jwt.InvalidTokenError:
        await websocket.close(code=1011, reason="Token inválido")
        return

    connected_clients.add(websocket)
    client_address = f"{websocket.remote_address[0]}:{websocket.remote_address[1]}"
    print(f"{client_address} conectado")

    try:
        async for message in websocket:
            print(f"[{client_address}] Mensaje recibido: {message}")
            if len(connected_clients) == 1:
                reversed_message = f"[Server] {message[::-1]}"
                print(f"[Server] Respondiendo: {reversed_message}")
                await websocket.send(reversed_message)
            else:
                annotated_message = f"[{client_address}] {message}"
                # Enviar a todos menos al que envió
                for client in connected_clients:
                    if client != websocket:
                        await client.send(annotated_message)
    except exceptions.ConnectionClosed as e:
        print(f"Conexión cerrada con {client_address}: {e}")
    finally:
        connected_clients.remove(websocket)
        print(f"{client_address} desconectado.")

async def websocket_server():
    server = await serve(handler, "0.0.0.0", 8765)
    print("Servidor WebSocket escuchando en el puerto 8765")
    await server.wait_closed()

async def login_handler(request):
    data = await request.json()
    username = data.get("username")
    password = data.get("password")

    # Validar credenciales (ejemplo sencillo)
    if username == "usuario" and password == "contraseña":
        # Generar un token JWT válido por 1 hora
        expiration = datetime.datetime.utcnow() + datetime.timedelta(hours=1)
        token = jwt.encode({"sub": username, "exp": expiration}, SECRET_KEY, algorithm="HS256")
        return web.json_response({"token": token})
    else:
        return web.json_response({"error": "Credenciales inválidas"}, status=401)

async def http_server():
    app = web.Application()
    app.router.add_post("/login", login_handler)
    runner = web.AppRunner(app)
    await runner.setup()
    site = web.TCPSite(runner, "0.0.0.0", 5000)
    print("Servidor HTTP escuchando en el puerto 5000 (para /login)")
    await site.start()

async def main():
    # Correr WebSocket y HTTP en paralelo
    await asyncio.gather(
        websocket_server(),
        http_server()
    )

asyncio.run(main())
