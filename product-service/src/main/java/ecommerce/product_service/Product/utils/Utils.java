package ecommerce.product_service.Product.utils;

import java.security.SecureRandom;

public class Utils {

    private static final String ALLPHANUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateCode (int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0; i<length; i++) {
            int randomIndex = secureRandom.nextInt(ALLPHANUMERIC_STRING.length());
            char randomChar =  ALLPHANUMERIC_STRING.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }
        return stringBuilder.toString();
    }
}
