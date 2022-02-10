import json

from flask import Flask, request, jsonify, make_response, abort
import sys
from flask_sqlalchemy import SQLAlchemy

from typing import List, Optional

app = Flask(__name__)

app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///recipes.db'
__db__ = SQLAlchemy(app)


"""
DATABASE
"""


class Ingredient(__db__.Model):
    __tablename__ = "ingredient"

    id = __db__.Column(__db__.Integer, primary_key=True)
    title = __db__.Column(__db__.String(80), nullable=False)
    measure = __db__.Column(__db__.String(10), nullable=False)
    amount = __db__.Column(__db__.Float, nullable=False)

    def __init__(self, title, measure, amount):
        self.title = title
        self.measure = measure
        self.amount = amount

    def to_dict(self) -> dict:
        return {"title": self.title, "measure": self.measure, "amount": self.amount}


class Recipe(__db__.Model):
    __tablename__ = "recipe"

    id = __db__.Column(__db__.Integer, primary_key=True)
    title = __db__.Column(__db__.String(80), nullable=False)
    description = __db__.Column(__db__.String(80), nullable=False)
    _directions = __db__.Column(__db__.String(500), nullable=False)

    def __init__(self, title, description, directions):
        self.title = title
        self.description = description
        self._directions = "|".join(directions)

    @property
    def directions(self) -> List[str]:
        return str(self._directions).split('|')

    @directions.setter
    def directions(self, value):
        self._directions = "|".join(value)

    def to_dict(self, ingredients, **kwargs):
        recipe_dict = {
            "title": self.title,
            "description": self.description,
            "directions": self.directions,
            "ingredients": ingredients
        }
        for i, j in kwargs.items():
            recipe_dict[f"{i}"] = j
        return recipe_dict


class RecipeIngredientContainer(__db__.Model):
    __tablename__ = "recipe_has_product"

    id = __db__.Column(__db__.Integer, primary_key=True)
    id_ingredient = __db__.Column(__db__.Integer, __db__.ForeignKey('ingredient.id'))
    id_recipe = __db__.Column(__db__.Integer, __db__.ForeignKey('recipe.id'))

    def __init__(self, id_ingredient, id_recipe):
        self.id_ingredient = id_ingredient
        self.id_recipe = id_recipe


__db__.create_all()  # creating all the tables


class Database:
    def __init__(self):
        pass

    @classmethod
    def add_recipe(cls, obj: dict):
        recipe = Recipe(obj.get("title"), obj.get("description"), obj.get("directions"))
        __db__.session.add(recipe)
        __db__.session.commit()
        for i in obj.get("ingredients"):
            ingredient = Ingredient(i.get("title"), i.get("measure"), i.get("amount"))
            __db__.session.add(ingredient)
            __db__.session.commit()
            __db__.session.add(RecipeIngredientContainer(ingredient.id, recipe.id))
            __db__.session.commit()
        return recipe.id

    @classmethod
    def update_recipe(cls, id_: int, obj: dict):
        recipe = Recipe.query.get(id_)
        if not recipe:
            return False
        ingredients_from_recipe = RecipeIngredientContainer.query.filter_by(id_recipe=id_).all()

        for i in ingredients_from_recipe:
            ingredient = Ingredient.query.get(i.id_ingredient)
            __db__.session.delete(ingredient)
            __db__.session.delete(i)
        __db__.session.commit()

        recipe.title = obj.get('title')
        recipe.description = obj.get('description')
        recipe.directions = obj.get('directions')
        __db__.session.commit()

        for i in obj.get('ingredients', []):
            ingredient = Ingredient(i.get("title"), i.get("measure"), i.get("amount"))
            __db__.session.add(ingredient)
            __db__.session.commit()
            __db__.session.add(RecipeIngredientContainer(ingredient.id, id_))
            __db__.session.commit()
        return True

    @classmethod
    def get_recipe(cls, id_: int = -1, ingredient_list: List[str] = None, max_directions: Optional[int] = None):
        if id_ != -1:
            recipe_ingredients = RecipeIngredientContainer.query\
                .filter(RecipeIngredientContainer.id_recipe == id_).all()
            if not recipe_ingredients:
                return None
            ingredients = []
            for i in recipe_ingredients:
                ingredient = Ingredient.query.get(i.id_ingredient)
                ingredients.append(ingredient.to_dict())
            recipe = Recipe.query.get(recipe_ingredients[0].id_recipe)
            return recipe.to_dict(ingredients)
        else:
            ingredients = Ingredient.query.filter(Ingredient.title.in_(ingredient_list)).all()
            ingredient_ids = [i.id for i in ingredients]
            recipe_holder = RecipeIngredientContainer.query\
                .filter(RecipeIngredientContainer.id_ingredient.in_(ingredient_ids)).all()
            if not recipe_holder:
                return {'error': 'No recipe here yet'}
            unique_recipes = list(set([i.id_recipe for i in recipe_holder]))

            recipes = []

            for i in unique_recipes:
                ingredients_for_recipe = RecipeIngredientContainer.query.filter_by(id_recipe=i).all()
                for j in ingredients_for_recipe:
                    if j.id_ingredient not in ingredient_ids:
                        break
                else:
                    recipe = Recipe.query.get(i)
                    recipe_ingredients_ids = [el.id for el in ingredients_for_recipe]
                    recipe_ingredients = [k.to_dict() for k in ingredients if k.id in recipe_ingredients_ids]
                    if (max_directions and len(recipe.directions) <= int(max_directions)) or not max_directions:
                        recipes.append(recipe.to_dict(recipe_ingredients, id=i))
            recipes.sort(key=lambda x: x.get('title'), reverse=True)
            return recipes  # if recipes else {'error': 'No recipe for these ingredients'}

    @classmethod
    def delete_recipe(cls, id_: int):
        recipe = Recipe.query.get(id_)
        if recipe:
            recipe_holder = RecipeIngredientContainer.query.filter_by(id_recipe=id_).all()
            ingredient_ids = [i.id_ingredient for i in recipe_holder]
            ingredients = Ingredient.query.filter(Ingredient.id.in_(ingredient_ids)).all()
            __db__.session.delete(recipe)
            for i in recipe_holder:
                __db__.session.delete(i)
            for ingredient in ingredients:
                __db__.session.delete(ingredient)
            __db__.session.commit()
            return 204
        return 404

    @classmethod
    def check_data(cls, obj):
        return obj.get('title', False) \
               and obj.get('description', False) \
               and obj.get('directions', False) \
               and obj.get('ingredients', False)


db = Database


# write your code here
@app.route('/api/recipe/<int:id_>/', methods=['GET', 'DELETE', 'PUT'])
def main(id_: int):
    if request.method == 'GET':
        recipe = db.get_recipe(id_)
        if recipe:
            return make_response(recipe, 200)
        else:
            return abort(404, "Illegal id.")
    elif request.method == 'DELETE':
        status = db.delete_recipe(id_)
        return make_response("No such recipe!", status)
    elif request.method == 'PUT':
        data = json.loads(request.json) if not isinstance(request.json, dict) else request.json
        if not db.check_data(data):
            return make_response("No Content", 400)
        status = db.update_recipe(id_, data)
        return make_response("No Content", 204 if status else 404)


@app.route('/api/recipe', methods=['GET'])
def get_by_ingredients():
    if request.method == 'GET':
        ingredients = request.args.get('ingredients', '').split('|')
        max_directions = request.args.get('max_directions', None)
        response = db.get_recipe(ingredient_list=ingredients, max_directions=max_directions)
        return make_response(jsonify(response))


@app.route("/api/recipe/new", methods=['POST'])
def post_new_recipe():
    data = json.loads(request.json) if not isinstance(request.json, dict) else request.json

    if not db.check_data(data):
        return make_response("No Content", 400)

    id_ = db.add_recipe(data)

    if id_ == -1:
        return make_response("Incorrect data!", 400)
    return make_response({"id": id_}, 200)


# don't change the following way to run flask:
if __name__ == '__main__':
    if len(sys.argv) > 1:
        arg_host, arg_port = sys.argv[1].split(':')
        app.run(host=arg_host, port=arg_port)
    else:
        app.run()
"""
if not recipes:
    return make_response(jsonify({'error': 'No recipe here yet'}), 200)
else:
    recipe_list = []
    for recipe in recipes.values():
        if recipe.get('ingredients', False) and ingredients:
            for i in recipe.get('ingredients'):
                if i.get("title", "non_informatory") not in ingredients:
                    break
            else:
                recipe_list.append(recipe)
    if recipe_list:
        return make_response(jsonify(recipe_list))
    else:
        return jsonify({'error': 'No recipe for these ingredients'})
"""
