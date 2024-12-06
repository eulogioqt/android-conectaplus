import asyncio
import websockets

async def echo_reverse(websocket, path):
    async for message in websocket:
        print(f"Mensaje recibido: {message}")
        reversed_message = message[::-1]
        print(f"Respondiendo: {reversed_message}")
        await websocket.send(reversed_message)

async def main():
    server = await websockets.serve(echo_reverse, "0.0.0.0", 8765)
    print("Servidor WebSocket escuchando en el puerto 8765")
    
    await server.wait_closed()

asyncio.run(main())
