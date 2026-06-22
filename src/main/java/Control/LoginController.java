package Control;
//primo controller di prova senza caricare il database
public class LoginController {

    public boolean login(String email, String password) {

        if (email == null || password == null) {
            return false;
        }

        return email.equals("admin@salus.it")
                && password.equals("Admin");
    }
}
