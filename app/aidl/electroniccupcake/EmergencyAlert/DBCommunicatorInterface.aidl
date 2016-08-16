// DBCommunicatorInterface.aidl
package electroniccupcake.EmergencyAlert;

// Declare any non-default types here with import statements

interface DBCommunicatorInterface
{
    String [] readDb();             // Reads values from DB, returns in name;phone;uri format
    void insertIntoDb(String name, String number, String URI);  // inserts vaues in the db
    void deleteRowFromDb(String number);    // Deletes the value from the database.
}
