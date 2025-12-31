package airline.model;

public class Person {
    protected String id;
    protected String name;
    protected String phone;

    
    // ------------------------ CONSTRUCTOR ------------------------
    public Person(String id, String name, String phone) {
        this.id = id.toUpperCase();
        this.name = name;
        this.phone = phone;
    }

    
    // ------------------------ GETTER METHODS ------------------------
    public String getId() { 
    	return id; 
    }
    public String getName() { 
    	return name; 
    }
    public String getPhone() { 
    	return phone; 
    }


    // ------------------------ SETTER METHODS ------------------------
    public void setName(String name) {
        if (name != null && !name.isEmpty()) this.name = name;
    }
    public void setPhone(String phone) {
        if (phone != null && !phone.isEmpty()) this.phone = phone;
    }
}
