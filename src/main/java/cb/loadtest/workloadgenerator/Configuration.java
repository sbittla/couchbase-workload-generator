package cb.loadtest.workloadgenerator;

import static java.lang.String.format;
import java.util.List;


/**
 * Configuration class is used to read the configuration settings through a <file name>.yml file
 * @author srinivasab
 *
 */
public final class Configuration {
	private String server;
	private String user;
	private String password;
    private String query;
    private String bucket;
    private boolean adhoc;
    private List< String > arguments;
    private int threads;


    /**
     * gets the server name from <file name>.yml
     * @return
     */
    public String getServer() {
        return server;
    }
    
    /**
     * sets the server variable 
     * @param server
     */
    public void setServer(String server) {
        this.server = server;
    }
    
    /**
     * gets the login user from <file name>.yml
     * @return
     */
    public String getUser() {
        return user;
    }
    
    /**
     * sets the user variable
     * @param user
     */
    public void setUser(String user) {
        this.user = user;
    }
    
    /**
     * gets the login password from <file name>.yml
     * getPassword 
     * @return
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * sets the user password variable
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * gets the bucket name from <file name>.yml
     * @return
     */
    public String getBucket() {
        return bucket;
    }
    
    /**
     * sets the bucket variable
     * @param bucket
     */
    public void setBucket(String bucket) {
        this.bucket = bucket;
    }
    
    /**
     * gets the SQL Query from <file name>.yml
     * @return
     */
    public String getQuery() {
        return query;
    }
    
    /**
     * sets the query variable
     * @param query
     */
    public void setQuery(String query) {
        this.query = query;
    }
    
    /**
     * gets the query adhoc property from <file name>.yml
     * @return
     */
    public boolean getAdhoc() {
        return adhoc;
    }

    /**
     * sets the adhoc variable
     * @param adhoc
     */
    public void setAdhoc(boolean adhoc) {
        this.adhoc = adhoc;
    }
    
    /**
     * Gets the number of threads from  <file name>.yml to run in workload 
     * @return
     */
    public int getThreads() {
        return threads;
    }
    
    /**
     * setThreads - sets the number of threads to a variable
     * @param threads
     */
    public void setThreads(int threads) {
        this.threads = threads;
    }

    /**
     * Gets the list of arguments to run the workload
     * @return
     */
    public List< String > getArguments() {
        return arguments;
    }

    /**
     * Sets the list of arguments to a variable
     * @param arguments
     */
    public void setArguments(List< String > arguments) {
        this.arguments = arguments;
    }


    /**
     * Overrides toString method of String class.
     * Stores all the argumets to string builder
     * Covert to string and returns
     * This method is used for only debugging purpose. To check the data.
     */
    @Override
    public String toString() {
        return new StringBuilder()
        		.append( format( "Server: %s\n", server ) )
        		.append( format( "User: %s\n", user ) )
        		.append( format( "Password: %s\n", password ) )
            .append( format( "Adhoc: %s\n", adhoc ) )
            .append( format( "Query: %s\n", query ) )
            .append( format( "Query arguments: %s\n", arguments ) )
            .append( format( "Thread: %d\n", threads ) )
            .toString();
    }
}
