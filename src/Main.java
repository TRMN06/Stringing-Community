import com.formdev.flatlaf.themes.FlatMacDarkLaf;

public class Main {
    public static void main(String[] args) {

        FlatMacDarkLaf.setup();

        HomePage an = new HomePage("Stringing Community");
        an.setBounds(0, 0, 2000, 2000);
        an.rendiVisibile(an);

    }
}