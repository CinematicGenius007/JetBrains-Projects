import sqlite3
import json


class InvalidFormatException(Exception):
    def __init__(self, *args):
        super().__init__(args)
        self.message = args[0] if args else 'Invalid parameters'


class Database:
    def __init__(self, db: str, ingredients: str = None, meals: str = None):
        self.db = db
        self.ingredients = ingredients
        self.meals = meals

        self.connection = self.build_connection()

        self.cursor = self.connection.cursor()

        self.init_tables()

    def build_connection(self) -> sqlite3.Connection:
        return sqlite3.connect(self.db)

    def commit(self):
        self.connection.commit()

    def close_connection(self):
        self.connection.close()

    def init_tables(self):
        self.cursor.execute("PRAGMA foreign_keys = ON;")

        with open("grandma_recipe.json") as data_file:
            data = json.load(data_file)

        for table in data:
            self.cursor.execute(f"""CREATE TABLE IF NOT EXISTS {table} (
                {table[:-1]}_id INTEGER PRIMARY KEY,
                {table[:-1]}_name TEXT UNIQUE {'NOT NULL' if 'measures' not in table else ''}
            );""")

        self.cursor.execute("""CREATE TABLE IF NOT EXISTS recipes (
            recipe_id INTEGER PRIMARY KEY,
            recipe_name TEXT NOT NULL,
            recipe_description TEXT
        );""")

        self.commit()

        try:
            for item in data:
                for element in data.get(item):
                    self.cursor.execute(f"INSERT INTO {item} ({item[:-1]}_name) values('{element}');")
        except sqlite3.IntegrityError:
            pass

        self.commit()

        self.cursor.execute("""CREATE TABLE IF NOT EXISTS serve (
            serve_id INTEGER PRIMARY KEY,
            recipe_id INTEGER NOT NULL,
            meal_id INTEGER NOT NULL,
            FOREIGN KEY (recipe_id) REFERENCES recipes (recipe_id),
            FOREIGN KEY (meal_id) REFERENCES meals (meal_id)
        );""")

        self.cursor.execute("""CREATE TABLE IF NOT EXISTS quantity (
            quantity_id INTEGER PRIMARY KEY,
            quantity INTEGER NOT NULL,
            measure_id INTEGER NOT NULL,
            ingredient_id INTEGER NOT NULL,
            recipe_id INTEGER NOT NULL,
            FOREIGN KEY (measure_id) REFERENCES measures (measure_id),
            FOREIGN KEY (ingredient_id) REFERENCES ingredients (ingredient_id),
            FOREIGN KEY (recipe_id) REFERENCES recipes (recipe_id)
        );""")

        self.commit()

    def process_recipes(self, recipe_name: str, recipe_description: str) -> int:
        new_cursor = self.cursor.execute(f"INSERT INTO recipes (recipe_name, recipe_description) values ("
                                         f"'{recipe_name}', "
                                         f"'{recipe_description}');")
        self.commit()
        return new_cursor.lastrowid

    def process_meals(self, meals: list, recipe_id: int):
        for meal_id in meals:
            self.cursor.execute(f"INSERT INTO serve (recipe_id, meal_id) values ({recipe_id}, {meal_id})")
        self.commit()

    def process_ingredients(self, recipe_id: int, ingredients: list):
        try:
            quantity = int(ingredients[0])
        except ValueError:
            print("Number!")
            raise InvalidFormatException

        if len(ingredients) == 2:
            measure_query = self.cursor.execute(
                f"SELECT measure_id FROM measures WHERE measure_name = '';").fetchall()
        elif len(ingredients) == 3:
            measure = ingredients[1]
            measure_query = self.cursor.execute(
                f"SELECT measure_id FROM measures WHERE measure_name LIKE '%{measure}%';").fetchall()
        else:
            print("Wrong parameters!")
            raise InvalidFormatException

        if len(measure_query) != 1:
            print("The measure is not conclusive!")
            raise InvalidFormatException
        measure_id = measure_query[0][0]
        ingredient_query = self.cursor.execute(f"SELECT ingredient_id FROM ingredients WHERE ingredient_name LIKE '%"
                                               f"{ingredients[len(ingredients) - 1]}%'").fetchall()
        if len(ingredient_query) != 1:
            print("Again!")
            raise InvalidFormatException
        ingredient_id = ingredient_query[0][0]

        self.cursor.execute(f"INSERT INTO quantity (quantity, measure_id, ingredient_id, recipe_id) values "
                            f"({quantity}, "
                            f"{measure_id}, {ingredient_id}, {recipe_id});")
        self.commit()

    def query_recipes(self):
        ingredient_list = [f"'{i}'" for i in self.ingredients.split(',')]
        ingredient_query = f"SELECT ingredient_id FROM ingredients WHERE ingredient_name in ({', '.join(ingredient_list)})"

        ingredient_ids = [str(ti[0]) for ti in self.cursor.execute(ingredient_query).fetchall()]
        meal_list = [f"'{i}'" for i in self.meals.split(',')]
        meal_query = f"SELECT meal_id FROM meals WHERE meal_name in ({', '.join(meal_list)})"

        meal_ids = [str(ti[0]) for ti in self.cursor.execute(meal_query).fetchall()]

        search_query = f"""SELECT 
                r.recipe_name 
            FROM 
                recipes r
            WHERE
                r.recipe_id in (
                    SELECT 
                        s.recipe_id 
                    FROM 
                        serve s 
                    WHERE 
                        s.meal_id in ({", ".join(meal_ids)})
                    INTERSECT
                    SELECT
                        q.recipe_id
                    FROM
                        quantity q
                    WHERE
                        q.ingredient_id = {ingredient_ids[0]}
                    {self.get_and(ingredient_ids, 1, 'q')}
                )
            ORDER BY
                r.recipe_name
        """

        query_result = self.cursor.execute(search_query).fetchall()

        if not query_result or len(ingredient_ids) != len(ingredient_list) or len(meal_ids) != len(meal_list):
            print("There are no such recipes in the database")
        else:
            print("Recipes selected for you:", ", ".join([ti[0] for ti in query_result]))

    def get_and(self, ls: list, index: int, alias: str) -> str:
        if index >= len(ls):
            return ''
        return f"""AND EXISTS (
            SELECT
                 q{index}.recipe_id
            FROM
                quantity q{index}
            WHERE
                {alias}.recipe_id = q{index}.recipe_id
            AND
                q{index}.ingredient_id = {ls[index]}
            {self.get_and(ls, index + 1, f'q{index}')}
        )"""

    def print_meals(self):
        for meal_id, meal_name in self.cursor.execute("SELECT meal_id, meal_name FROM meals;"):
            print(f"{meal_id}) {meal_name} ", end="")
        print()
