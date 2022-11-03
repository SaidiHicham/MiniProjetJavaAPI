package com.miniProjet.projet.tools;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Tools {

    public static String generateRandomPassword(int len)
    {
        // ASCII range – alphanumeric (0-9, a-z, A-Z)
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < len; i++)
        {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }

        return sb.toString();
    }

    public int RandomFrom6to10() {
        List<Integer> givenList = Arrays.asList(6, 7, 8, 9, 10);
        Random rand = new Random();
        return givenList.get(rand.nextInt(givenList.size()));
    }

    public String RandomAdminAndUser() {
        List<Integer> givenList = Arrays.asList(1, 2);
        Random rand = new Random();
        if(givenList.get(rand.nextInt(givenList.size())) == 1)
            return "admin";
        return "user";
    }

}
