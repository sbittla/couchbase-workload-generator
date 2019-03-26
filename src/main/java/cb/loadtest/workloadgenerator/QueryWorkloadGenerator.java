package cb.loadtest.workloadgenerator;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.yaml.snakeyaml.Yaml;

import com.couchbase.client.java.*;
import com.couchbase.client.java.document.*;
import com.couchbase.client.java.document.json.*;
import com.couchbase.client.java.query.*;
import com.couchbase.client.java.query.consistency.ScanConsistency;


/**
 * Class QueryWorkloadGenerator - generates the load on couchbase server
 *  with configuration <file name>.yml as a command line argument 
 * @author srinivasab
 *
 */
public class QueryWorkloadGenerator {

	/**
	 * getRandomString - gets the Random string from a given array 
	 * @param array
	 * @return
	 */
	private static String getRandomString(String[] array) {
	    int rnd = new Random().nextInt(array.length);
	    return array[rnd];
	}
	/**
	 * getRandomSalaryInRange - in minimum to maximum range
	 * @param min
	 * @param max
	 * @return
	 */
	private static int getRandomSalaryInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
	
	/**
	 * populateData - populates the data based on the bucket name and number of rows
	 * @param bucket
	 * @param rows
	 */
	private static void populateData(Bucket bucket, int rows) {
		String[] departments = {"finance", "sales", "marketing"};
		String[] employed = {"yes", "no"};

        for (int i=0; i<rows; i++) {
            // Create a JSON Document
            String iformat = String.format("%03d",i);
            System.out.println("The iformat is:" + iformat);
            JsonObject emp = JsonObject.create()
                .put("name", "Srini" + iformat)
                .put("department", getRandomString(departments))
                .put("salary", getRandomSalaryInRange(5000,20000))
                	.put("employed", getRandomString(employed));

            // Store the Document
            bucket.upsert(JsonDocument.create("u:awesome_sri" + iformat, emp));

            // Load the Document and print it
            // Prints Content and Metadata of the stored Document
            System.out.println(bucket.get("u:awesome_sri" + iformat));           
        }
        
        //Create primary index 
        System.out.println("Creating index for bucket : " + bucket.name());
        try {
            // Create a N1QL Primary Index (but ignore if it exists)
            bucket.bucketManager().createN1qlPrimaryIndex(true, false);
        }catch (Exception e) {
        		System.out.println(e.getMessage());
        }
	}
	

	/**
	 * runQuery - Run the query based on bucket name and return results
	 * @param bucket
	 * @param adhoc
	 * @param query
	 * @param argument
	 * @return
	 */
	public static N1qlQueryResult runQuery(Bucket bucket, 
										  boolean adhoc,
										  String query,
										  String argument) {
		

        //Tokenize the argument and assign to variables
        String[] empDetails = argument.split(",");
        String department = empDetails[0].trim();
        int salary  = Integer.valueOf(empDetails[1].trim());
        String employed = empDetails[2].trim();
        
        //Pass adhoc as a parameter
        N1qlParams params = N1qlParams.build().adhoc(adhoc).consistency(ScanConsistency.STATEMENT_PLUS);        		

        //Run the N1qlQuery
        N1qlQueryResult result = bucket.query(        			
                N1qlQuery.parameterized(query,
                JsonArray.from(department,salary,employed), params)
            );        
        return result;
	}
	
	/**
	 * generateWorkload - Runs the load based on config <file name>.yml file settings
	 * @param config
	 */
	public static void generateWorkload(Bucket bucket, Configuration config) {
		//Initialize the variables read from <file name>.yml file
		final boolean adhoc = config.getAdhoc();
		final String query = config.getQuery();
		final List <String> arguments = config.getArguments();
		final Bucket bt = bucket;
		int threads = Integer.valueOf(config.getThreads());
        
        // Covert list of arguments to Array
		final String[] arrArgs = arguments.toArray(new String[0]);

        //Initialize the threads
        Thread[] arrThreads = new Thread[threads];
        
        // Run Multi Threaded Query execution

        for(int i=0; i<threads; i++){
	        	Thread thrd = new Thread("" + i){
	  	        public void run(){
		  	        	while(true) {
		  	        		try {
			  	        		System.out.println("Thread: " + getName() + " running");
			  	        		
			  	        		//Get Random argument from an Array of arguments
			  	        		final String argument = getRandomString(arrArgs);
			  	        		
			  	        		// Use the result however you want to use it.
			  	        		final N1qlQueryResult result = runQuery (bt, adhoc, query, argument);
			  	        		
			  	            //For debugging purpose, I have writing the output to the console
			  	            // We may need to comment this section of debug statement while running a load test.
			  	        		/*System.out.println("Results for the argument is: " + argument);
			  	        		for (N1qlQueryRow row : result) {
			  	        			System.out.println(row);
			  	        		}*/
		  	        		} catch (Throwable t) {
		  	        			System.err.println("Uncaught exception is detected! for Thread" + getName() + " "+ t
		  	                          + " st: " + Arrays.toString(t.getStackTrace()));
		  	        		}
		  	        	}
	  	        }
	  	      };
	  	    //Check whether the thread is not active. 
	  	    //If it is not active restart.  
	  	    if(!thrd.isAlive())  {
		        	thrd.start();
		        	arrThreads[i] = thrd;
	  	    }
        }

        
        // Wait for threads to complete
        try {
	  	  for (int i = 0; i < threads; i++) {
	  	      arrThreads[i].join(); 
	  	      // restart the runnable
	  	      Thread thrd = new Thread(arrThreads[i]);
	  	      thrd.start();
	  	  }
        }
	  	catch (Exception e) {
	  		System.out.println(e.getMessage());
	  	  }
     }



	/**
	 * Runs the main method with a <file name>.yml file input as an commandline argument
	 * @param args
	 * @throws Exception
	 */
    public static void main(String[] args) throws Exception {
    		if(( args.length < 2 ) || ( args.length > 3 )) {
            System.out.println( "Usage: java -jar couchbase-workload-generator.jar <file.yml> <populateData / runWorkload> <number of rows to populate>" );
            return;
        }
    		String yamlFile = args[ 0 ];
    		String runOption = args[1].toLowerCase();
            
        Yaml yaml = new Yaml();
        try {
        		//Load <file name.yml> file into config object
            InputStream in = Files.newInputStream( Paths.get( yamlFile ) );
            Configuration config = yaml.loadAs( in, Configuration.class );
            
            // Initialize the Connection
            Cluster cluster = CouchbaseCluster.create(config.getServer());
            cluster.authenticate(config.getUser(), config.getPassword());
            Bucket bucket = cluster.openBucket(config.getBucket());
            
            // Run the load test based on <file name>.yml file
            if (runOption.equals("populatedata")) {
            		int rowsToPopulate = 500;
            		if (args.length == 3) {
            			rowsToPopulate = Integer.valueOf( args[ 2 ] );
            		}
            		populateData(bucket, rowsToPopulate);
            } else if (runOption.equals("runworkload")) {
            		generateWorkload(bucket, config);
            } else {
            		System.out.println("ERROR: Invalid input choose populateData / runWorkload");
            }
            
            // Cleanup the bucket and cluster
            bucket.close();
            cluster.disconnect();
          } catch (Exception e) {
            System.out.println(e.getMessage());
          }    	
    }
}
