package modelo;

import org.mindrot.jbcrypt.BCrypt;

public class Hash {

    /* Retorna un hash bcrypt a partir de un texto */
    public static String bcrypt(String txt) {
        return BCrypt.hashpw(txt, BCrypt.gensalt());
    }

    /* Verifica si el texto proporcionado coincide con el hash bcrypt almacenado */
    public static boolean checkpw(String txt, String storedHash) {
        return BCrypt.checkpw(txt, storedHash);
    }

}

