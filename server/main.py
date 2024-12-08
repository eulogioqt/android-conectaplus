import asyncio
import websockets

connected_clients = set()

async def handler(websocket, path):
    connected_clients.add(websocket)
    client_address = f"{websocket.remote_address[0]}:{websocket.remote_address[1]}"
    try:
        async for message in websocket:
            print(f"[{client_address}] Mensaje recibido: {message}")
            if len(connected_clients) == 1:
                reversed_message = f"[Server] {message[::-1]}"
                print(f"[Server] Respondiendo: {reversed_message}")
                await websocket.send(reversed_message)
            else:
                annotated_message = f"[{client_address}] {message}"
                for client in connected_clients:
                    if client != websocket:
                        await client.send(annotated_message)
    except websockets.exceptions.ConnectionClosed as e:
        print(f"Conexión cerrada con {client_address}: {e}")
    finally:
        connected_clients.remove(websocket)
        print(f"{client_address} desconectado.")

async def main():
    server = await websockets.serve(handler, "0.0.0.0", 8765)
    print("Servidor WebSocket escuchando en el puerto 8765")
    await server.wait_closed()

asyncio.run(main())
