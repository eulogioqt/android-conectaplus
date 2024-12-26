import asyncio
import websockets
import random

# Consistencia:
# Al salirse un jugador que se borre su match y se notifique al otro jugador con DISCONNECT
# No pueda crear varias partidas un jugador que esta en partida
# No se pueda unir un jugador a varias partidas a la vez

connected_clients = {}
matches = {}

class MESSAGES:
    CHAT = "CHAT"
    CREATE = "CREATE"
    JOIN = "JOIN"
    MOVE = "MOVE"
    END = "END"
    START = "START"
    DENY = "DENY"
    DISCONNECT = "DISCONNECT"

async def process_message(websocket, m):
    client_address = get_socket_addr(websocket)
    print(f"[{client_address}] {m}")

    if is_message_type(m, MESSAGES.CHAT):
        msg = m[(len(MESSAGES.CHAT) + 1):]

        match_code = get_match_code(client_address)
        if match_code is not None:
            other_player = get_other_player(client_address, match_code)
            other_client = connected_clients.get(other_player)
            
            print(f"[{client_address}] Manda el mensaje {msg} en la sala {match_code}")
            await send_message(other_client, MESSAGES.CHAT, msg)
    
    elif is_message_type(m, MESSAGES.CREATE):
        code = None
        while code is None or code in matches.keys():
            code = str(random.randint(1000, 9999))

        matches[code] = { "j1": client_address }

        print(f"[{client_address}] Ha creado una nueva sala: {code}")
        await send_message(websocket, MESSAGES.CREATE, code)

    elif is_message_type(m, MESSAGES.JOIN):
        code = m[(len(MESSAGES.JOIN) + 1):]

        if code in matches.keys() and client_address != matches.get(code).get("j1") and matches.get(code).get("j2") is None:
            matches[code]["j2"] = client_address
            print(f"[{client_address}] Se ha unido a la sala {code}")

            j1 = connected_clients.get(matches.get(code).get("j1"))
            j2 = connected_clients.get(matches.get(code).get("j2"))

            await send_message(j1, MESSAGES.START)
            await send_message(j2, MESSAGES.START)
        else:
            await send_message(websocket, MESSAGES.DENY)

    elif is_message_type(m, MESSAGES.MOVE):
        x = m[(len(MESSAGES.MOVE) + 1):]

        match_code = get_match_code(client_address)
        if match_code is not None:
            other_player = get_other_player(client_address, match_code)
            other_client = connected_clients.get(other_player)
            
            print(f"[{client_address}] Suelta ficha en {x} en la sala {match_code}")
            await send_message(other_client, MESSAGES.MOVE, x)

    elif is_message_type(m, MESSAGES.END):
        match_code = get_match_code(client_address)
        if match_code is not None:
            matches.pop(match_code)

    else:
        await send_message(websocket, MESSAGES.CHAT, "[Server] ES EL FIN DEL MUNDO NO HAS MANDADO UN CHAT")
    
    print(matches)

def get_match_code(client_address):
    for match_code, actual_match in matches.items():
        if client_address in [actual_match.get("j1"), actual_match.get("j2")]:
            return match_code
    return None

def get_other_player(client_address, match_code):
    match = matches.get(match_code)
    if match is not None:
        j1, j2 = match.get("j1"), match.get("j2")
        if client_address in [j1, j2]:
            return j1 if client_address == j2 else j2
    return None

def get_socket_addr(websocket):
    return f"{websocket.remote_address[0]}:{websocket.remote_address[1]}"

def is_message_type(message, type):
    return message.startswith(type)

async def send_message(websocket, type, message=""):
    try:
        await websocket.send(type + " " + message)
    except Exception as e:
        print("Error enviando: " + str(e))

async def handler(websocket, path):
    client_address = get_socket_addr(websocket)
    connected_clients[client_address] = websocket
    print(f">> {client_address} ha entrado ({len(connected_clients.keys())} conexiones).")
    
    try:
        async for m in websocket:
            await process_message(websocket, m)
    except websockets.exceptions.ConnectionClosed as e:
        print(f"Conexión cerrada con {client_address}: {e}")
    finally:
        connected_clients.pop(client_address)
        print(f">> {client_address} ha salido ({len(connected_clients.keys())} conexiones).")

async def main():
    server = await websockets.serve(handler, "0.0.0.0", 8765)
    print("Servidor WebSocket escuchando en el puerto 8765")
    await server.wait_closed()

asyncio.run(main())
