import random

print("""H A N G M A N""")

choice = random.choice(['python', 'java', 'kotlin', 'javascript'])
string = ['-'] * len(choice)
attempted = []

LIFE = 8
MENU = input('Type "play" to play the game, "exit" to quit: ')


def check_input(input_: str) -> (bool, str):
    if len(input_) != 1:
        return False, "You should input a single letter"
    elif input_.isalpha() and input_.islower():
        return True, ""
    else:
        return False, "Please enter a lowercase English letter"


while MENU != 'exit':
    if MENU == 'play':
        while LIFE > 0:
            print('\n{}'.format("".join(string)))
            guess = input("Input a letter: ").strip()
            status, error = check_input(guess)
            if not status:
                print(error)
                continue
            if guess in attempted:
                print("You've already guessed this letter")
                continue
            if guess in choice:
                attempted.append(guess)
                indexes = [i for i in range(len(choice)) if choice[i] == guess]
                for j in indexes:
                    string[j] = guess
            else:
                print("That letter doesn't appear in the word")
                attempted.append(guess)
                LIFE -= 1

            if string.count('-') == 0:
                print(f'You guessed the word {choice}!')
                break

        print('You lost!\n' if string.count('-') != 0 else 'You survived!\n')
    MENU = input('Type "play" to play the game, "exit" to quit: ')
