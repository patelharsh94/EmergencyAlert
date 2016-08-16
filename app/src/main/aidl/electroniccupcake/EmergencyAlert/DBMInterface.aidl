// DBMInterface.aidl
package electroniccupcake.EmergencyAlert;

// Declare any non-default types here with import statements

interface DBMInterface
{
    // inserts the values into the DB.
    void insertIntoDB(String name, String phoneNumber, String URI, int pos);

    // removes from DB
    void deleteFromDB(String name);

    // resets the index of a certain item in a list
    void setIndex(String name, int index);

    // returns a list of phone numbers from the db.
    String [] getPhoneNumberList();

    // returns a list of URI's.
    String [] getURIList();

    // returns a list of names.
    String [] getNameList();

    // returns a list of index.
    int [] getIndexList();
}
