import argparse

from db import Database, InvalidFormatException

parser = argparse.ArgumentParser(description="This is nuts!")
parser.add_argument("db_file", type=str)
parser.add_argument("--ingredients", type=str, required=False)
parser.add_argument("--meals", type=str, required=False)

args = parser.parse_args()

db = Database(args.db_file, args.ingredients, args.meals)

if args.ingredients and args.meals:
    db.query_recipes()
else:
    print("Pass the empty recipe name to exit.")

    while True:
        recipe_name = input("Recipe name: ")
        if not recipe_name:
            break
        recipe_description = input("Recipe description: ")
        last_id = db.process_recipes(recipe_name, recipe_description)

        db.print_meals()

        meals_to_serve = list(map(int, input("Enter proposed meals separated by a space: ").split(" ")))
        db.process_meals(meals_to_serve, last_id)

        while True:
            ingredient = input("Input quantity of ingredients <press enter to stop>: ").strip().split(" ")

            if not ingredient or not ingredient[0]:
                break
            else:
                try:
                    db.process_ingredients(last_id, ingredient)
                except InvalidFormatException as ife:
                    print(ife.message)
                    continue

db.close_connection()
