package aau.distributedsystems;

public class Master {

    public static void main(String[] args) {
        if(args.length < 3) {
            System.out.println("the number of arguments does not match the required amount");
            System.out.println("params are: port, max-slave-number, waiting-timeout");
            return;
        }


    }

    boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
