package com.wesave.back;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

@RestController
public class InputRessource {

	@Autowired
	private CarRepository CarRepository;

	@Autowired
	private RentalRepository RentalRepository;

	private String rootPath = "C:/GIT/wesave/backend/backend";


				// Intro de l'énoncé de l'exercice :
				/* # Intro

				We are building a peer-to-peer car rental service. Let's call it Drivy :)

				Here is our plan:

				- Let any car owner list her car on our platform
				- Let any person (let's call him 'driver') book a car for given dates/distance
				*/



	@PostMapping("level1/input")
	public String newLevel1Input(@RequestBody String jsonInput) {

		String responseStr = "";

		try {

			// On récupère le json envoyé par la requête POST
			// On analyse le json tree de l'input

			ObjectMapper mapper = new ObjectMapper();

			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			JsonNode jsonNodeRoot = mapper.readTree(jsonInput);
			JsonNode jsonNodeCars = jsonNodeRoot.get("cars");
			JsonNode jsonNodeRentals = jsonNodeRoot.get("rentals");

			// Pour chaque élément json de cars, on enregistre un objet car

			for(JsonNode jsonNode : jsonNodeCars)
			{
				CarRepository.save(mapper.readValue(jsonNode.toString(),Car.class));
			}

			// Pour chaque élément json de rentals, on enregistre un objet rental avec le prix calculé de la location


			JSONArray rentals = new JSONArray();

			for(JsonNode jsonNode : jsonNodeRentals)
			{

				Rental rental = mapper.readValue(jsonNode.toString(),Rental.class);
				int price = 0;

				Optional<Car> carRent = CarRepository.findById(rental.getCar_id());

				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");


				try {
					Date startDate = formatter.parse(rental.getStart_date());

					Date endDate = formatter.parse(rental.getEnd_date());

					long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
					long nbDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) + 1;

					//	règles qui correspondent à l'énoncé du level1 :

					/*

					# Level 1

					The car owner chooses a price per day and price per km for her car.
					The driver then books the car for a given period and an approximate distance.

					The rental price is the sum of:

					- A time component: the number of rental days multiplied by the car's price per day
					- A distance component: the number of km multiplied by the car's price per km

					*/

					price += (carRent.get().getPrice_per_day() * nbDays) + carRent.get().getPrice_per_km() * rental.getDistance();

					rental.setPrice(price);

					//*********************


					Rental savedRental = RentalRepository.save(rental);

					JsonObject savedRentalJson = new JsonObject();
					savedRentalJson.addProperty("price", savedRental.getPrice());
					savedRentalJson.addProperty("id", savedRental.getId());
					rentals.put(new JSONObject(savedRentalJson.toString()));

				} catch (ParseException e) {
					e.printStackTrace();
					throw new DateFormatException();
				}

			}

			JSONObject response = new JSONObject();

			response.put("rentals", rentals);

			Object jsonResult = mapper.readValue(response.toString(), Object.class);

			String indented = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonResult);

			responseStr = indented.toString();

			// on écrit le résultat dans un fichier output.json dans le dossier data du dossier de ce level

			FileWriter file = new FileWriter(rootPath + "/level1/data/output.json");
			file.write(responseStr);
			file.close();

		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return responseStr;
	}




	@PostMapping("level2/input")
	public String newLevel2Input(@RequestBody String jsonInput) {

		String responseStr = "";

		try {

			// On récupère le json envoyé par la requête POST
			//	On analyse le json tree de l'input

			ObjectMapper mapper = new ObjectMapper();

			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			JsonNode jsonNodeRoot = mapper.readTree(jsonInput);
			JsonNode jsonNodeCars = jsonNodeRoot.get("cars");
			JsonNode jsonNodeRentals = jsonNodeRoot.get("rentals");

			// Pour chaque élément json de cars, on enregistre un objet car

			for(JsonNode jsonNode : jsonNodeCars)
			{
				CarRepository.save(mapper.readValue(jsonNode.toString(),Car.class));
			}

			// Pour chaque élément json de rentals, on enregistre un objet rental avec le prix calculé de la location

			JSONArray rentals = new JSONArray();

			for(JsonNode jsonNode : jsonNodeRentals)
			{

				Rental rental = mapper.readValue(jsonNode.toString(),Rental.class);
				int price = 0;

				Optional<Car> carRent = CarRepository.findById(rental.getCar_id());

				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");


				try {
					Date startDate = formatter.parse(rental.getStart_date());

					Date endDate = formatter.parse(rental.getEnd_date());

					long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
					long nbDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) + 1;

					//	règles qui correspondent à l'énoncé du level2 :

					/*# Level 2

					To be as competitive as possible, we decide to have a decreasing pricing for longer rentals.

					New rules:

					- price per day decreases by 10% after 1 day
					- price per day decreases by 30% after 4 days
					- price per day decreases by 50% after 10 days

					Adapt the rental price computation to take these new rules into account.
					 */

					long totalDaysPrice = 0;

					for(int i = 0; i < nbDays; i++)
					{
						double decrease = 0.0;

						if(i+1 > 10)
							decrease = 0.5;
						else if(i+1 > 4)
							decrease = 0.3;
						else if(i+1 > 1)
							decrease = 0.1;

						totalDaysPrice += carRent.get().getPrice_per_day() * (1-decrease);
					}

					price += totalDaysPrice + (carRent.get().getPrice_per_km() * rental.getDistance());

					rental.setPrice(price);

					//***

					Rental savedRental = RentalRepository.save(rental);

					JsonObject savedRentalJson = new JsonObject();
					savedRentalJson.addProperty("price", savedRental.getPrice());
					savedRentalJson.addProperty("id", savedRental.getId());
					rentals.put(new JSONObject(savedRentalJson.toString()));

				} catch (ParseException e) {
					e.printStackTrace();
					throw new DateFormatException();
				}

			}

			JSONObject response = new JSONObject();

			response.put("rentals", rentals);

			Object jsonResult = mapper.readValue(response.toString(), Object.class);

			String indented = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonResult);

			responseStr = indented.toString();

			// on écrit le résultat dans un fichier output.json dans le dossier data du dossier de ce level

			FileWriter file = new FileWriter(rootPath + "/level2/data/output.json");
			file.write(responseStr);
			file.close();

		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return responseStr;
	}





	@PostMapping("level3/input")
	public String newLevel3Input(@RequestBody String jsonInput) {

		String responseStr = "";

		try {

			// On récupère le json envoyé par la requête POST
			//	On analyse le json tree de l'input

			ObjectMapper mapper = new ObjectMapper();

			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			JsonNode jsonNodeRoot = mapper.readTree(jsonInput);
			JsonNode jsonNodeCars = jsonNodeRoot.get("cars");
			JsonNode jsonNodeRentals = jsonNodeRoot.get("rentals");

			// Pour chaque élément json de cars, on enregistre un objet car

			for(JsonNode jsonNode : jsonNodeCars)
			{
				CarRepository.save(mapper.readValue(jsonNode.toString(),Car.class));
			}

			// Pour chaque élément json de rentals, on enregistre un objet rental avec le prix calculé de la location

			JSONArray rentals = new JSONArray();

			for(JsonNode jsonNode : jsonNodeRentals)
			{

				Rental rental = mapper.readValue(jsonNode.toString(),Rental.class);
				int price = 0;

				Optional<Car> carRent = CarRepository.findById(rental.getCar_id());

				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");


				try {
					Date startDate = formatter.parse(rental.getStart_date());

					Date endDate = formatter.parse(rental.getEnd_date());

					long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
					long nbDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) + 1;

					long totalDaysPrice = 0;

					for(int i = 0; i < nbDays; i++)
					{
						double decrease = 0.0;

						if(i+1 > 10)
							decrease = 0.5;
						else if(i+1 > 4)
							decrease = 0.3;
						else if(i+1 > 1)
							decrease = 0.1;

						totalDaysPrice += carRent.get().getPrice_per_day() * (1-decrease);
					}

					price += totalDaysPrice + (carRent.get().getPrice_per_km() * rental.getDistance());

					rental.setPrice(price);

					Rental savedRental = RentalRepository.save(rental);

					JsonObject savedRentalJson = new JsonObject();
					savedRentalJson.addProperty("price", savedRental.getPrice());
					savedRentalJson.addProperty("id", savedRental.getId());

					// règles qui correspondent à l'énoncé du level3 :

					/*# Level 3

					The car owner now wants her money.
					We decide to take a 30% commission on the rental price to cover our costs and have a solid business model.

					The commission is split like this:

					- half goes to the insurance
					- 100€/day goes to the roadside assistance	// ICI, ERREUR DANS l'ENONCE INITIAL DE DRIVY,
															// POUR QUE CA CORRESPONDE AU JSON FOURNI output.json IL FAUT 100€/jour
					- the rest goes to us

					Compute the amount that belongs to the insurance, to the assistance and to us.
					 */

					// calcul et ajout de la commission détaillée dans le json de l'output

					double totalCommission = 0.3 * price;

					double insuranceFee = 0.5 * totalCommission;

					double assistanceFee = nbDays * 100;

					double drivyFee = totalCommission - (insuranceFee + assistanceFee);

					JsonObject commissionJson = new JsonObject();
					commissionJson.addProperty("insurance_fee", (int) insuranceFee);
					commissionJson.addProperty("assistance_fee", (int) assistanceFee);
					commissionJson.addProperty("drivy_fee", (int) drivyFee);

					savedRentalJson.add("commission", commissionJson);

					//***

					rentals.put(new JSONObject(savedRentalJson.toString()));

				} catch (ParseException e) {
					e.printStackTrace();
					throw new DateFormatException();
				}

			}

			JSONObject response = new JSONObject();

			response.put("rentals", rentals);

			Object jsonResult = mapper.readValue(response.toString(), Object.class);

			String indented = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonResult);

			responseStr = indented.toString();

			// on écrit le résultat dans un fichier output.json dans le dossier data du dossier de ce level

			FileWriter file = new FileWriter(rootPath + "/level3/data/output.json");
			file.write(responseStr);
			file.close();

		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return responseStr;
	}




	@PostMapping("level4/input")
	public String newLevel4Input(@RequestBody String jsonInput) {

		String responseStr = "";

		try {

			// On récupère le json envoyé par la requête POST
			//	On analyse le json tree de l'input

			ObjectMapper mapper = new ObjectMapper();

			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			JsonNode jsonNodeRoot = mapper.readTree(jsonInput);
			JsonNode jsonNodeCars = jsonNodeRoot.get("cars");
			JsonNode jsonNodeRentals = jsonNodeRoot.get("rentals");

			// Pour chaque élément json de cars, on enregistre un objet car

			for(JsonNode jsonNode : jsonNodeCars)
			{
				CarRepository.save(mapper.readValue(jsonNode.toString(),Car.class));
			}

			// Pour chaque élément json de rentals, on enregistre un objet rental avec le prix calculé de la location

			JSONArray rentals = new JSONArray();

			for(JsonNode jsonNode : jsonNodeRentals)
			{

				Rental rental = mapper.readValue(jsonNode.toString(),Rental.class);
				int price = 0;

				Optional<Car> carRent = CarRepository.findById(rental.getCar_id());

				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");


				try {
					Date startDate = formatter.parse(rental.getStart_date());

					Date endDate = formatter.parse(rental.getEnd_date());

					long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
					long nbDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) + 1;

					long totalDaysPrice = 0;

					for(int i = 0; i < nbDays; i++)
					{
						double decrease = 0.0;

						if(i+1 > 10)
							decrease = 0.5;
						else if(i+1 > 4)
							decrease = 0.3;
						else if(i+1 > 1)
							decrease = 0.1;

						totalDaysPrice += carRent.get().getPrice_per_day() * (1-decrease);
					}

					price += totalDaysPrice + (carRent.get().getPrice_per_km() * rental.getDistance());

					rental.setPrice(price);

					Rental savedRental = RentalRepository.save(rental);

					// règles qui correspondent à l'énoncé du level4 :

					/*# Level 4

					We now want to know how much money must be debited/credited for each actor, this will allow us to debit or pay them accordingly.
					 */

					// on refactore le json de l'output

					JSONObject savedRentalJson = new JSONObject();
					savedRentalJson.put("id", savedRental.getId());

					double totalCommission = 0.3 * price;

					double insuranceFee = 0.5 * totalCommission;

					double assistanceFee = nbDays * 100;

					double drivyFee = totalCommission - (insuranceFee + assistanceFee);

					JSONArray actionsJson = new JSONArray();

					JSONObject actionJsonDriver = new JSONObject();
					actionJsonDriver.put("who", "driver");
					actionJsonDriver.put("type", "debit");
					actionJsonDriver.put("amount", price);
					actionsJson.put(actionJsonDriver);

					JSONObject actionJsonOwner = new JSONObject();
					actionJsonOwner.put("who", "owner");
					actionJsonOwner.put("type", "credit");
					actionJsonOwner.put("amount", (0.7 * price));
					actionsJson.put(actionJsonOwner);

					JSONObject actionJsonInsurance = new JSONObject();
					actionJsonInsurance.put("who", "insurance");
					actionJsonInsurance.put("type", "credit");
					actionJsonInsurance.put("amount", insuranceFee);
					actionsJson.put(actionJsonInsurance);

					JSONObject actionJsonAssistance = new JSONObject();
					actionJsonAssistance.put("who", "assistance");
					actionJsonAssistance.put("type", "credit");
					actionJsonAssistance.put("amount", assistanceFee);
					actionsJson.put(actionJsonAssistance);

					JSONObject actionJsonDrivy = new JSONObject();
					actionJsonDrivy.put("who", "drivy");
					actionJsonDrivy.put("type", "credit");
					actionJsonDrivy.put("amount", drivyFee);
					actionsJson.put(actionJsonDrivy);

					savedRentalJson.put("actions", actionsJson);

					//***

					rentals.put(new JSONObject(savedRentalJson.toString()));

				} catch (ParseException e) {
					e.printStackTrace();
					throw new DateFormatException();
				}

			}

			JSONObject response = new JSONObject();

			response.put("rentals", rentals);

			Object jsonResult = mapper.readValue(response.toString(), Object.class);

			String indented = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonResult);

			responseStr = indented.toString();

			// on écrit le résultat dans un fichier output.json dans le dossier data du dossier de ce level

			FileWriter file = new FileWriter(rootPath + "/level4/data/output.json");
			file.write(responseStr);
			file.close();

		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return responseStr;
	}





	@PostMapping("level5/input")
	public String newLevel5Input(@RequestBody String jsonInput) {

		String responseStr = "";

		try {

			// On récupère le json envoyé par la requête POST
			//	On analyse le json tree de l'input

			ObjectMapper mapper = new ObjectMapper();

			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			// règles qui correspondent à l'énoncé du level5 :

			/*# Level 5

			Some drivers want to be able to buy additionnal features after their booking.

			Here are the possible options:
			- GPS: 5€/day, all the money goes to the owner
			- Baby Seat: 2€/day, all the money goes to the owner
			- Additional Insurance: 10€/day, all the money goes to Drivy

			_This is the final level, now would be a good time to tidy up your code and do a last round of refactoring :)_

			*/

			// on refactore le json de l'output

			JsonNode jsonNodeRoot = mapper.readTree(jsonInput);
			JsonNode jsonNodeCars = jsonNodeRoot.get("cars");
			JsonNode jsonNodeRentals = jsonNodeRoot.get("rentals");
			JsonNode jsonNodeOptions = jsonNodeRoot.get("options"); // ajout pour le level5

			// Pour chaque élément json de cars, on enregistre un objet car

			for(JsonNode jsonNode : jsonNodeCars)
			{
				CarRepository.save(mapper.readValue(jsonNode.toString(),Car.class));
			}

			// Pour chaque élément json de rentals, on enregistre un objet rental avec le prix calculé de la location

			JSONArray rentals = new JSONArray();

			for(JsonNode jsonNode : jsonNodeRentals)
			{

				Rental rental = mapper.readValue(jsonNode.toString(),Rental.class);
				int price = 0;

				Optional<Car> carRent = CarRepository.findById(rental.getCar_id());

				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");


				try {
					Date startDate = formatter.parse(rental.getStart_date());

					Date endDate = formatter.parse(rental.getEnd_date());

					long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
					long nbDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) + 1;

					long totalDaysPrice = 0;

					for(int i = 0; i < nbDays; i++)
					{
						double decrease = 0.0;

						if(i+1 > 10)
							decrease = 0.5;
						else if(i+1 > 4)
							decrease = 0.3;
						else if(i+1 > 1)
							decrease = 0.1;

						totalDaysPrice += carRent.get().getPrice_per_day() * (1-decrease);
					}

					price += totalDaysPrice + (carRent.get().getPrice_per_km() * rental.getDistance());

					rental.setPrice(price);

					Rental savedRental = RentalRepository.save(rental);

					JSONObject savedRentalJson = new JSONObject();
					savedRentalJson.put("id", savedRental.getId());

					// ajout pour le level5

					ArrayList<String> options = new ArrayList();

					boolean hasGps = false;
					boolean hasBabySeat = false;
					boolean hasAdditionalInsurance = false;


					for(JsonNode jsonNodeOption : jsonNodeOptions)
					{
						int id = Integer.parseInt(jsonNodeOption.get("id").toString());
						int rentalId = Integer.parseInt(jsonNodeOption.get("rental_id").toString());
						String type = jsonNodeOption.get("type").toString();

						if(rentalId == savedRental.getId())
						{
							options.add(type.toString().replaceAll("\"", ""));
							if(type.toString().replaceAll("\"", "").equals("gps"))
								hasGps = true;
							else if(type.toString().replaceAll("\"", "").equals("baby_seat"))
								hasBabySeat = true;
							else if(type.toString().replaceAll("\"", "").equals("additional_insurance"))
								hasAdditionalInsurance = true;
						}
					}

					savedRentalJson.put("options", options);

					double totalCommission = 0.3 * price;

					double insuranceFee = 0.5 * totalCommission;

					double assistanceFee = nbDays * 100;

					double drivyFee = totalCommission - (insuranceFee + assistanceFee);


					double ownerAmount = (0.7 * price);

					// on ajoute le prix des options du level5
					if(hasGps)
					{
						price += 500 * nbDays;
						ownerAmount += 500 * nbDays;
					}
					if(hasBabySeat)
					{
						price += 200 * nbDays;
						ownerAmount += 200 * nbDays;
					}
					if(hasAdditionalInsurance)
					{
						price += 1000 * nbDays;
						drivyFee += 1000 * nbDays;
					}



					//***

					JSONArray actionsJson = new JSONArray();

					JSONObject actionJsonDriver = new JSONObject();
					actionJsonDriver.put("who", "driver");
					actionJsonDriver.put("type", "debit");
					actionJsonDriver.put("amount", price);
					actionsJson.put(actionJsonDriver);

					JSONObject actionJsonOwner = new JSONObject();
					actionJsonOwner.put("who", "owner");
					actionJsonOwner.put("type", "credit");
					actionJsonOwner.put("amount", ownerAmount);
					actionsJson.put(actionJsonOwner);

					JSONObject actionJsonInsurance = new JSONObject();
					actionJsonInsurance.put("who", "insurance");
					actionJsonInsurance.put("type", "credit");
					actionJsonInsurance.put("amount", insuranceFee);
					actionsJson.put(actionJsonInsurance);

					JSONObject actionJsonAssistance = new JSONObject();
					actionJsonAssistance.put("who", "assistance");
					actionJsonAssistance.put("type", "credit");
					actionJsonAssistance.put("amount", assistanceFee);
					actionsJson.put(actionJsonAssistance);

					JSONObject actionJsonDrivy = new JSONObject();
					actionJsonDrivy.put("who", "drivy");
					actionJsonDrivy.put("type", "credit");
					actionJsonDrivy.put("amount", drivyFee);
					actionsJson.put(actionJsonDrivy);

					savedRentalJson.put("actions", actionsJson);

					//***

					rentals.put(new JSONObject(savedRentalJson.toString()));

				} catch (ParseException e) {
					e.printStackTrace();
					throw new DateFormatException();
				}

			}

			JSONObject response = new JSONObject();

			response.put("rentals", rentals);

			Object jsonResult = mapper.readValue(response.toString(), Object.class);

			String indented = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonResult);

			responseStr = indented.toString();

			// on écrit le résultat dans un fichier output.json dans le dossier data du dossier de ce level

			FileWriter file = new FileWriter(rootPath + "/level5/data/output.json");
			file.write(responseStr);
			file.close();

		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// on renvoie le résultat au navigateur également

		return responseStr;
	}

}
