package tn.esprit.utils;

import javax.mail.MessagingException;

public class TEST {
    public static void main(String[] args) throws MessagingException {
        String m="saifeddinefrikhi@gmail.com";
        String n="Objectif testing mail";
        String o="test";
        Mail mail= new Mail();
        mail.sendEmail(m,n,o);
    }
}
