from typing import List


def game_state(cells: List[str]):
    x_count, o_count, _count = cells.count('X'), cells.count('O'), cells.count('_')
    if abs(x_count - o_count) > 1:
        return 'Impossible'

    winner = []

    for i in range(3):
        if cells[i * 3] == cells[i * 3 + 1] == cells[i * 3 + 2] != '_' \
                or cells[i] == cells[i + 3] == cells[i + 6] != '_':
            winner.append(f"{cells[i]} wins")

    if cells[0] == cells[4] == cells[8] != '_' or cells[2] == cells[4] == cells[6] != '_':
        winner.append(f"{cells[4]} wins")

    if len(winner) >= 1:
        return winner[0] if len(winner) == 1 else "Impossible"

    if _count > 0:
        return "Game not finished"
    return "Draw"
    

def flip(coin: str):
    return 'O' if coin == 'X' else 'X'
    

board = list("_________")  # list(input('Enter cells: ').strip())


def show(cells):
    matrix = [cells[i * 3:i * 3 + 3] for i in range(3)]

    print("---------")
    for i in matrix:
        print(f"| {' '.join([j if j != '_' else ' ' for j in i])} |")
    print("---------")


turn = 'X'
state = game_state(board)
show(board)

while state == "Game not finished":
    try:
        x, y = tuple(map(int, input("Enter the coordinates: ").strip().split()))
        if 1 <= x <= 3 and 1 <= y <= 3:
            if board[(x - 1) * 3 + y - 1] == '_':
                board[(x - 1) * 3 + y - 1] = turn
                turn = flip(turn)
                state = game_state(board)
                show(board)
                if state != "Game not finished":
                    print(state)
                    break
            else:
                print("This cell is occupied! Choose another one!")
        else:
            print("Coordinates should be from 1 to 3!")
    except ValueError:
        print("You should enter numbers!")
