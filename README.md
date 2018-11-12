web API RESTful Spring Boot

L'exercice a été traité en java

nécessite MySQL local avec une BDD nommée "wesave" créée,
modifier src/main/ressources/application.properties pour infos de connexion de votre serveur MySQL (user/mdp)

Les 5 exercices sont traités par l'API,
une fois déployée chaque exercice est accessible à un url :

http://localhost:5000/level1/input

http://localhost:5000/level2/input

http://localhost:5000/level3/input

http://localhost:5000/level4/input

http://localhost:5000/level5/input

L'envoi d'un input au format json dans une requête http POST à chaque url entraîne une réponse contenant le json attendu en output pour l'exercice considéré
(on peut utiliser POSTMAN pour Chrome par exemple pour envoyer les requêtes POST)

Les méthodes codant ces réponses sont dans la classe :
drivy/src/main/java/com/wesave/back/InputRessource.java


****** ENONCE INITIAL :

# Drivy Backend Challenge

Looking for a job? Check out our [open positions](https://en.drivy.com/jobs).
You can also take a look at our [engineering blog](https://drivy.engineering/) to learn more about the way we work.

## Guidelines

**For each level, write code that generates a `data/output.json` file from `data/input.json`.
An `expected_output.json` file is available to give you a reference on what result is expected.**

- Clone this repo (do **not** fork it)
- Solve the levels in ascending order
- Only do one commit per level

## Pointers

You can have a look at the higher levels, but please do the simplest thing that could work for the level you're currently solving.

The levels become more complex over time, so you will probably have to re-use some code and adapt it to the new requirements.
A good way to solve this is by using OOP, adding new layers of abstraction when they become necessary and possibly write tests so you don't break what you have already done.

Don't hesitate to write [shameless code](http://red-badger.com/blog/2014/08/20/i-spent-3-days-with-sandi-metz-heres-what-i-learned/) at first, and then refactor it in the next levels.

For higher levels we are interested in seeing code that is clean, extensible and robust, so don't overlook edge cases, use exceptions where needed, ...

Please also note that:

- All prices are stored as integers (in cents)
- Running `$ ruby main.rb` from the level folder should generate the desired output, but of course feel free to add more files if needed.

## Sending Your Results

Once you are done, please send your results to someone from Drivy.

- If you are already in discussion with us, send it directly to the person you are talking to.
- If not, use the application form [on every job listing](https://en.drivy.com/jobs).

You can send your Github project link or zip your directory and send it via email.
If you do not use Github, don't forget to attach your `.git` folder.

Good luck!
