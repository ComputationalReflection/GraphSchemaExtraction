package es.uniovi.reflection.graph;

public class Parameters {
    private static final String COPYRIGHT_MESSAGE =  "Graph Schema Extractor 1.0.0 - Computational Reflection Research Group (University of Oviedo)\n";

    private static final String HELP_MESSAGE = COPYRIGHT_MESSAGE + "\nOptions:\n" +
            "\t-help\n\t\tDisplays this usage message (Short form: -?).\n" +
            "\t-neo4j_user=<user_name>\n\t\tNEO4J User name. (Default value is neo4j, short form:-nu=<user_name>)\n" +
            "\t-neo4j_password=<user_password>\n\t\tNEO4J User password. (Short form:-np=<user_pasword>)\n" +
            "\t-neo4j_host=<host>\n\t\tNEO4J Host address. (Short form:-nh=<host>)\n" +
            "\t-neo4j_port_number=<port_number>\n\t\tNEO4J Port number. (Default value is 7687, short form:-npn=<port_number>)\n" +
            "\t-neo4j_database=<database_name>\n\t\tNEO4J Database name. (Short form:-ndb=<database_name>)\n" +
            "\t-output=<file_path>\n\t\tOutput file path. (Default value is output.json, Short form:-o=<file_path>)\n" +
            "\n";

    protected String copyrightMessage;
    protected String helpMessage;
    protected String errorMessage;
    protected String noHostMessage;
    protected String noPasswordMessage;
    protected String noDataBaseMessage;
    protected String unknownNEO4JModeMessage;
    protected static final String DEFAULT_NEO4J_PORT_NUMBER = "7687";
    protected static final String DEFAULT_NEO4J_USER = "neo4j";
    protected static final String DEFAULT_OUTPUT_FILE = "output.json";
    protected static final String[] HELP_OPTIONS = { "help" , "?" };
    protected static final String[] NEO4J_DATABASE_OPTIONS = { "neo4j_database","ndb" };
    protected static final String[] NEO4J_USER_OPTIONS = { "neo4j_user","nu" };
    protected static final String[] NEO4J_PASSWORD_OPTIONS = { "neo4j_password","np" };
    protected static final String[] NEO4J_HOST_OPTIONS = { "neo4j_host","nh" };
    protected static final String[] NEO4J_PORT_NUMBER_OPTIONS = { "neo4j_port_number","npn" };
    protected static final String[] OUTPUT_OPTIONS = { "output","o" };
    protected static final String[] OPTIONS_PREFIX = { "-" };
    protected static final String[] OPTIONS_ASSIGNMENT = { "=" };
    public String neo4j_database = "";
    public String neo4j_user = DEFAULT_NEO4J_USER;
    public String neo4j_password = "";
    public String neo4j_host = "";
    public String output_file = DEFAULT_OUTPUT_FILE;
    public String neo4j_port_number = DEFAULT_NEO4J_PORT_NUMBER;

    protected Parameters(){
        this(COPYRIGHT_MESSAGE,HELP_MESSAGE);
    }

    protected Parameters(String copyrightMessage, String helpMessage){
        this.copyrightMessage = copyrightMessage;
        this.helpMessage = helpMessage;
        this.errorMessage = copyrightMessage + "\nSome error in the input parameters. Type -help for help.\n";
        this.noHostMessage = copyrightMessage + "\nNo NEO4J host specified. Type -help for help.\n";
        this.noPasswordMessage = copyrightMessage + "\nNo NEO4J user password specified. Type -help for help.\n";
        this.noDataBaseMessage = copyrightMessage + "\nNo NEO4J database specified. Type -help for help.\n";
        this.unknownNEO4JModeMessage = copyrightMessage + "\nUnknown neo4j mode option. Type -help for help.\n";
    }

    public static Parameters parseArguments(String[] args) {
        Parameters parameters = new Parameters();
        for (String parameter : args) {
            parameters.parseParameter(parameter);
        }
        if (parameters.neo4j_database.isEmpty()) {
            System.out.println(parameters.noDataBaseMessage);
            System.exit(2);
        }
        if (parameters.neo4j_host.isEmpty()) {
            System.out.println(parameters.noHostMessage);
            System.exit(2);
        }
        if (parameters.neo4j_password.isEmpty()) {
            System.out.println(parameters.noPasswordMessage);
            System.exit(2);
        }
        return parameters;
    }

    protected void parseParameter(String parameter) {
        for (String parameterPrefix : Parameters.OPTIONS_PREFIX) {
            if (parameter.startsWith(parameterPrefix)) {
                if(this.parseOption(parameter.substring(parameterPrefix.length())))
                    return;
            }
        }
    }
    protected boolean parseOption(String option) {
        final String lowerCaseOption = option.toLowerCase();
        for (String opString : Parameters.HELP_OPTIONS) {
            if (lowerCaseOption.equals(opString)) {
                System.out.println(this.helpMessage);
                System.exit(0);
            }
        }
        for (String opString : Parameters.NEO4J_DATABASE_OPTIONS) {
            if (lowerCaseOption.startsWith(opString)) {
                this.neo4j_database = parseValue(lowerCaseOption.substring(opString.length()));
                return true;
            }
        }
        for (String opString : Parameters.NEO4J_USER_OPTIONS) {
            if (lowerCaseOption.startsWith(opString)) {
                this.neo4j_user = parseValue(option.substring(opString.length()));
                return true;
            }
        }
        for (String opString : Parameters.NEO4J_PASSWORD_OPTIONS) {
            if (lowerCaseOption.startsWith(opString)) {
                this.neo4j_password = parseValue(option.substring(opString.length()));
                return true;
            }
        }
        for (String opString : Parameters.NEO4J_HOST_OPTIONS) {
            if (lowerCaseOption.startsWith(opString)) {
                this.neo4j_host = parseValue(option.substring(opString.length()));
                return true;
            }
        }
        for (String opString : Parameters.NEO4J_PORT_NUMBER_OPTIONS) {
            if (lowerCaseOption.startsWith(opString)) {
                this.neo4j_port_number = parseValue(option.substring(opString.length()));
                return true;
            }
        }
        for (String opString : Parameters.OUTPUT_OPTIONS) {
            if (lowerCaseOption.startsWith(opString)) {
                this.output_file = parseValue(option.substring(opString.length()));
                return true;
            }
        }
        return false;
    }

    protected String parseValue(String value) {
        for (String opAssignment : Parameters.OPTIONS_ASSIGNMENT)
            if (value.startsWith(opAssignment))
                return value.substring(opAssignment.length());
        System.err.println(this.errorMessage);
        System.exit(2);  // 2 == Bad option assignment
        return null;
    }
}
