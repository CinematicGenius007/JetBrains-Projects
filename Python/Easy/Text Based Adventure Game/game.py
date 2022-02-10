import json
import os
import re
from enum import Enum
from typing import Union

with open(os.path.join(os.getcwd(), 'stories/story.json'), 'r') as f:
    data = json.load(f)


class State(Enum):
    START = 1
    CHARACTER = 2
    GAME = 3
    END = 4


class User:
    class Character:
        def __init__(
                self,
                name=None,
                species=None,
                gender=None
        ):
            self.name = name
            self.species = species
            self.gender = gender

        def __str__(self):
            return "Your character: {}\n".format(", ".join([self.name,
                                                            self.species,
                                                            self.gender]))

    class Inventory:
        def __init__(
                self,
                snack=None,
                weapon=None,
                tool=None,
                extra=[]
        ):
            self.snack = snack
            self.weapon = weapon
            self.tool = tool
            self.extra = extra

        def __str__(self):
            return 'Your inventory: {}\n'.format(", ".join([self.snack,
                                                            self.weapon,
                                                            self.tool]))

    def __init__(
            self,
            character_=None,
            inventory=None,
            difficulty=None,
            life=1,
            level=1
    ):
        self.character = character_
        self.inventory = inventory
        self.difficulty = difficulty
        self.life = life
        self.level = level

    def __str__(self):
        return "Good luck on your journey!\n" + str(self.character) + \
               str(self.inventory) + "Difficulty: {}\n".format(
            self.difficulty) + "Number of lives: {}\n".format(self.life)

    def to_dict(self):
        return {
            'char_attrs': {
                'name': self.character.name,
                'species': self.character.species,
                'gender': self.character.gender
            },
            'inventory': {
                'snack': self.inventory.snack,
                'weapon': self.inventory.weapon,
                'tool': self.inventory.tool
            },
            'difficulty': self.difficulty,
            'lives': self.life,
            'level': self.level
        }


STATE = State.START
DIFFICULTY = {'1': 'Easy', '2': 'Medium', '3': 'Hard'}
LIVES = dict(zip(DIFFICULTY.values(), [5, 3, 1]))
USER: Union[User, None] = None
USERNAME = ""


def start() -> State:
    global USER
    global USERNAME
    print("***Welcome to Jumanji***\n")
    print("1- Press key '1' or type 'start' to start a new game",
          "2- Press key '2' or type 'load' to load your progress",
          "3- Press key '3' or type 'quit' to quit the game", sep='\n')
    user_input = input()
    if re.match('(^1$|^start$)', user_input.lower()):
        print('Starting a new game...')
        return State.CHARACTER
    elif re.match('(^2$|^load$)', user_input.lower()):
        user_files = os.listdir(os.path.join(os.getcwd(), 'game/saves'))
        if user_files:
            print("Type your user name from the list:")
            for user_file in user_files:
                print(user_file.split('.')[0])
            user_file_input = input()
            if os.path.isfile(os.path.join(os.getcwd(), f'game/saves/'
                                                          f'{user_file_input}.json')):
                with open(os.path.join(os.getcwd(), f'game/saves/{user_file_input}.json')) \
                        as f_:
                    user_data = json.load(f_)
                    USER = User(
                        User.Character(
                            user_data["char_attrs"]['name'],
                            user_data['char_attrs']['species'],
                            user_data['char_attrs']['gender']
                        ),
                        User.Inventory(
                            user_data['inventory']['snack'],
                            user_data['inventory']['weapon'],
                            user_data['inventory']['tool']
                        ),
                        user_data['difficulty'],
                        user_data['lives'],
                        user_data['level']
                    )
                    print("Loading your progress...")
                    print(f"Level {USER.level}", '\n')
                    return State.GAME
            else:
                print("No save data found!")
                return State.START
        else:
            print("No save data found!")
            return State.START
    elif re.match('^(^3$|^quit$)', user_input.lower()):
        print('Goodbye')
        return State.END
    elif re.match('/q', user_input.lower()):
        print('You sure you want to quit the game? Y/N', end=' ')
        confirmation = input()
        if re.match('y', confirmation.lower()):
            return State.END
        elif not re.match('n', confirmation.lower()):
            print("Unknown input! Please enter a valid one. \n")
            return State.START
    else:
        print('Unknown input! please enter a valid one')
        return State.START


def character() -> State:
    global USER
    print("Enter a user name to save your progress or type '/b' to go back ")
    user_input = input()
    global USERNAME
    USERNAME = user_input
    if re.match(r'^/b$', user_input.lower()):
        print('Going back to menu...')
        return State.START
    else:
        user = User(User.Character(), User.Inventory())
        print('Create your character:')
        print("1- Name ", end='')
        user.character.name = input()
        print("2- Species ", end='')
        user.character.species = input()
        print("3- Gender ", end='')
        user.character.gender = input()

        print('Pack your bag for the journey:')
        print("1- Favourite Snack ", end='')
        user.inventory.snack = input()
        print("2- A weapon for the journey ", end='')
        user.inventory.weapon = input()
        print("3- A traversal tool ", end='')
        user.inventory.tool = input()

        print("Choose your difficulty:\n"
              "1- Easy \n"
              "2- Medium \n"
              "3- Hard")
        while True:
            difficulty = input()
            if re.match('^([123]|easy|medium|hard)$', difficulty.lower()):
                user_difficulty = DIFFICULTY[
                    difficulty] if difficulty.isdigit() else difficulty.capitalize()
                user.difficulty = user_difficulty
                user.life = LIVES[user_difficulty]

                USER = user
                break
            else:
                print("Unknown input! Please enter a valid one. \n")
        print(user)
        return State.GAME


def help_():
    print("""Type the number of the option you want to choose.
Commands you can use:
/i => Shows inventory.
/q => Exits the game.
/c => Shows the character traits.
/h => Shows help.\n""")


def save():
    with open(os.path.join(os.getcwd(), f'game/saves/'
                                        f'{USERNAME}.json'), 'w') \
            as f_:
        f_.write(json.dumps(USER.to_dict(), indent=4))


def gameplay():
    scene = 1  # Possible stages 0 1 2
    # choice = 1
    lvl = USER.level
    # outcome = 1
    next_ = True
    while lvl <= 3:
        if next_:
            print(data['stories'][f'lvl{lvl}']['scenes'][f'scene{scene}'])
            print("\nWhat will you do? Type the number of the option or type "
                  "'/h' to show help.\n")

            choices = [f'{i}- {choice}' for i, choice in enumerate(
                data['choices'][f'lvl{lvl}'][f'scene{scene}'].values(), 1)]
            print(*choices, sep='')
            next_ = False
        user_input = input()
        if re.match('/h', user_input.lower()):
            help_()
        elif re.match('/i', user_input.lower()):
            print(USER.inventory)
        elif re.match('/c', user_input.lower()):
            print(USER.character, end='')
            print(f'Lives remaining: {USER.life}')
        elif re.match('/q', user_input.lower()):
            print('You sure you want to quit the game? Y/N', end=' ')
            confirmation = input()
            if re.match('y', confirmation.lower()):
                break
            elif not re.match('n', confirmation.lower()):
                print("Unknown input! Please enter a valid one. \n")
        elif user_input.isdigit() and 1 <= int(user_input) <= 3:
            outcome = data['outcomes'][f'lvl{lvl}'][f'scene{scene}'][
                f'outcome{user_input}']
            if isinstance(outcome, dict):
                if 'key' in USER.inventory.extra:
                    outcome = outcome['option1']
                else:
                    outcome = outcome['option2']
            if isinstance(outcome, str):
                action = re.search(r'\(.*\)', outcome)
                new_outcome = re.sub(r'\(.*\)', '', outcome)
                if '{' in new_outcome:
                    new_outcome = new_outcome.format(tool=USER.inventory.tool)
                print(new_outcome)
                if re.match(r'.*repeat.*', action.group(0)):
                    next_ = True
                if re.match(r'.*inventory.*', action.group(0)):
                    if 'key' in USER.inventory.extra:
                        USER.inventory.extra.remove('key')
                    else:
                        USER.inventory.extra.append('key')

                if re.match(r'.*life-1.*', action.group(0)):
                    USER.life -= 1
                    if USER.life <= 0:
                        print("You died! Lives remaining:  0\n"
                              "You've run out of lives! Game over!\n")
                        # print(f'Level {USER.level}')
                        return State.START
                    else:
                        print(f"You died! Lives remaining: {USER.life}")
                        print(f"Level {USER.level}\n\n")
                        scene = 1
                        next_ = True
                if re.match(r'.*life\+1.*', action.group(0)):
                    USER.life += 1
                    print(f"You gained an extra life! Lives remaining: "
                          f"{USER.life}")
                    scene += 1
                    next_ = True
                if re.match(r'.*move.*', action.group(0)):
                    scene += 1
                    next_ = True
                if re.match(r'.*save.*', action.group(0)):
                    USER.level += 1
                    lvl += 1
                    scene = 1
                    print(f'\nLevel {USER.level}\n')
                    save()
                    print()
                    next_ = True
                    # break
        else:
            print("Unknown input! Please enter a valid one. \n")
    else:
        print("Congratulations! You beat the game!")
    print('Goodbye!')
    return State.END


def end() -> State:
    return State.END


while STATE != State.END:
    if STATE == State.START:
        STATE = start()
    elif STATE == State.CHARACTER:
        STATE = character()
    elif STATE == State.GAME:
        STATE = gameplay()
    else:
        STATE = end()

