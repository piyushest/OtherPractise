/*
 * This is the class to hold a single object,
 * I have defined this class so that in future if,
 * team needs to extend the application, and if we want to
 * get more data for a product as styles or anything we 
 * will do not need to change the code a lot 
 * we just need to capture that information and store it
 * in the Object for each product and add the relevant method
 * in the Solution class for whatever we need to do.
 * 
 * Presently i have just defined defaultImageUrl and sku value
 * that i need for this application and storing that value.
 * sku value to put the name of the image.
 * */
public class Product {
	private String defaultImageUrl;
	private String sku;

	public void setSku(String skuValue) {
		sku = skuValue;
	}

	public String getSku() {
		return sku;
	}

	public void setDefaultImageUrl(String url) {
		defaultImageUrl = url;
	}

	public String getDefaultImageUrl() {
		return defaultImageUrl;
	}

}
