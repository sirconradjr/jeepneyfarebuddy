package mmcm.shs.jeepneyfarebuddy;

public class User {

    private String fullname, dateofbirth, contactnumber, emailaddress;
    private int age;

    public User(){

        this.fullname = "";
        this.dateofbirth = "";
        this.contactnumber = "";
        this.emailaddress = "";
        this.age = 0;

    }

    public void setFullname(String fname){
        this.fullname = fname;
    }

    public String getFullname(){
        return fullname;
    }

    public void setdob(String fdob){
        this.dateofbirth = fdob;
    }

    public String getdob(){
        return dateofbirth;
    }

    public void setAge(int age){
        this.age = age;
    }

    public int getAge(){
        return age;
    }

    public void setEmailaddress(String femail){
        this.emailaddress = femail;
    }

    public String getEmailaddress(){
        return emailaddress;
    }

    public void setContactnumber(String fcontact){
        this.emailaddress = fcontact;
    }

    public String setContactnumber(){
        return contactnumber;
    }

}
