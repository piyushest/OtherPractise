import java.util.ArrayList;

/*
 * Note: this is the entry point of the application containing the main method
 * The input file has to be given to this class as argument and
 * which will be passed to the method and it will begin the execution
 * please go through the complete documentation in the rest of the classes.
 * 
 * Result:
 * By using the file that i was provided as input i was getting 4 images.
 * */
public class Tests {

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.err
					.println("Please restart the application by giving the file as command line argument");
		}
		Solution s = new Solution();
		// method to get the skus
		ArrayList<String> skus = s.getAllSkus("C:/Users/Piyush/Desktop/zappos/javaccskuz.txt");
		// method to get the image links
		ArrayList<Product> productsList = s.getTheImageLinks(skus);
		// method to download the image
		s.downloadImage(productsList);
	}

}
