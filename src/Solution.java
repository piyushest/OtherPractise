import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/*
 * Please note:
 * Please go through the full documentation for this application
 * for each method functionality please see above the method defination.
 *  
 * Algorithm:
 * First step is reading the file containing the sku's
 * and storing them in a List.
 * Things to do in the first step make sure there is no space
 * in the starting of sku and ending of sku.
 * 
 * Second step is 
 * Make the call for each sku to the api
 * and read the response and store it in a string.
 * Convert that string into JsonObject and get the 
 * image link store it in a Map or list.
 * 
 * Third step call each link and get the image and store it 
 * to the desired folder.
 * */

public class Solution {

	final static String key = "52ddafbe3ee659bad97fcce7c53592916a6bfd73";

	/*
	 * This method will read all the skus in the given file and store them in a
	 * List. In this method if the input file does not contain any skus than
	 * this method will throw an exception. Telling that there is no sku found
	 * in the file. I kept this behaviour as it does not make sense to proceed
	 * ahead in the application if there is no sku's found in the file. If
	 * anyone else is using this method in any other api its the responsibility
	 * to handle this exception.
	 * 
	 * I have also make sure that no duplicate sku is entered in the sku list.
	 */
	public ArrayList<String> getAllSkus(String file) throws Exception {
		ArrayList<String> skus = new ArrayList<String>();
		String current;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while ((current = reader.readLine()) != null) {

				// triming the sku string as it might contain any extra spaces
				String filtered = current.trim();

				if (filtered.length() == 0) {
					continue;
				} else {
					// checking the duplicates if the same sku is encountered
					// we will not store again in the list.
					if (!skus.contains(filtered)) {
						skus.add(filtered);
					}
				}
			}

			reader.close();
		} catch (Exception e) {
			System.err.println(e);
		}

		if (skus.size() == 0) {
			throw new Exception(
					"NO SKUS FOUND IN THE FILE,PLEASE CHECK THE FILE CONTENTS");

		}
		return skus;
	}

	/*
	 * This method will make the api call and get the Product information and
	 * retrieve the defaultImageURL and create a Object of Product and store the
	 * defaultImageUrl in the object. I am taking the information in json format
	 * and parsing that json to get the url. To make sure that i am getting the
	 * correct response i am checking if the response is not 200 that means ok i
	 * am moving ahead to the next sku response.
	 * 
	 * There were /Product/<SKU> /Product/7564933 /Product/7564933,7590514
	 * /Product?id=["7564933","7590514"] ways defined by the API but i cannot
	 * use the multiple sku's together as using them together if anyone of them
	 * misbehave and give wrong response. than even the correct sku response is
	 * not received.
	 */
	public ArrayList<Product> getTheImageLinks(ArrayList<String> skus)
			throws Exception {
		// checking the list null or empty
		if (skus == null) {
			throw new Exception("list of skus cannot be null");
		}

		if (skus.size() == 0) {
			throw new Exception("list of skus cannot be empty");
		}

		ArrayList<Product> productsList = new ArrayList<Product>();
		try {
			for (String sku : skus) {
				StringBuilder singleJson = new StringBuilder();
				URL url = new URL("http://api.zappos.com/Product/" + sku
						+ "?includes=[%22styles%22]&key=" + key);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Accept", "application/json");
				if (connection.getResponseCode() != 200) {
					continue;
				}

				// Reading the response and adding it together.
				// Used StringBuilder as it is mutable and it will not
				// create string again and again.
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				String response;
				while ((response = reader.readLine()) != null) {
					singleJson.append(response);
				}

				// converting the string in json Object
				JSONObject root = new JSONObject(singleJson.toString());
				JSONArray products = root.getJSONArray("product");
				for (int i = 0; i < products.length(); i++) {
					JSONObject product = products.getJSONObject(i);
					String scheck = product.get("defaultImageUrl").toString();
					if (scheck == "null") {
						continue;
					}

					/*
					 * getting the product information and creating a product
					 * for each sku(i have already checked the sku duplicate).so
					 * for each sku different product has to be defined.
					 */
					Product singleProduct = new Product();
					singleProduct.setDefaultImageUrl(scheck);

					// storing sku for the name of the image file.
					singleProduct.setSku(sku);
					productsList.add(singleProduct);
				}
			}

		} catch (Exception e) {
			System.err.println(e);
		}
		return productsList;
	}

	/*
	 * This method will create the image directory under the current directory
	 * and than will iterate over all the products and get the url to download
	 * the image.
	 * 
	 * For the name of the image i have saved the sku corresponding to that
	 * particular product and giving the name of the image same as that of sku
	 * value.
	 */
	public void downloadImage(ArrayList<Product> productsList) throws Exception {
		if (productsList == null) {
			throw new Exception("list of products cannot be null");
		}

		if (productsList.size() == 0) {
			throw new Exception("list of products cannot be empty");
		}

		try {
			/*
			 * create a directory if does not exists under the current directory
			 */
			File directory = new File("images");
			if (!directory.exists()) {
				directory.mkdir();
			}

			/*
			 * Iterating over all the products and getting the defaultImageURL
			 */
			for (Product singleProduct : productsList) {
				// Taking the url in singleURL so that i dont have to
				// look up into the Object again and again.
				String singleUrl = singleProduct.getDefaultImageUrl();
				URL url = new URL(singleUrl);

				// getting the file name for getting the extension
				String file = url.getFile();
				int extensionPoint = file.lastIndexOf(".");
				String extension = file.substring(extensionPoint + 1,
						file.length());

				// Creating the stream to write the image.
				InputStream input = new BufferedInputStream(url.openStream());
				OutputStream output = new FileOutputStream("images/"
						+ singleProduct.getSku() + "." + extension);
				byte[] data = new byte[2048];
				int length;
				while ((length = input.read(data)) != -1) {
					output.write(data, 0, length);
				}

				// flush the output but not closing it
				// because i do not find the idea
				// of closing it again and again
				output.flush();
			}

		} catch (Exception e) {
			System.err.println(e);
		}
	}
}
