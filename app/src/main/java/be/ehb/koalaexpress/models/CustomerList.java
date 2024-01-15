package be.ehb.koalaexpress.models;

import java.util.ArrayList;

public class CustomerList {

    public ArrayList<Customer> mList;

    public CustomerList() {
        mList = new ArrayList<Customer>();
    }

    public Customer LoginCustomer(String naam, String paswoord){
        for (Customer c : mList) {
            if(c.mLogin.toLowerCase().equals(naam.toLowerCase()) || c.mEmail.toLowerCase().equals(naam.toLowerCase()) && c.mPassword.equals(paswoord)){
                return c;
            }
        }
        return null;
    }
}
