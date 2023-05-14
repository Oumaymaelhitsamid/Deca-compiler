import java.lang.Math;

public class cordic {

    private static double K = 0.6072529350088812561694;
    private static int N = 32;

    public static double[] loop(double x0, double y0, double z0, int select, int N) {
        double x = x0 * K;
        double y = y0 * K;
        double z = select == 2 ? 0 : z0;
        double _x, d = 1, e;
        for (int i = 0; i < N; i++) {
            e = Math.pow(2, -i);
            if (select == 0) { // atan
                d = (y >= 0 ? -1 : 1);
            } else if (select == 1) { // sin&cos
                d = (z >= 0 ? 1 : -1);
            } else if (select == 2) { // asin
                d = (y < z0 ? 1 : -1);
            }
            _x = x - d * y * e;
            y = y + d * x * e;
            x = _x;
            if (select == 2) {
                z = z + d * Math.atan(e);
            } else {
                z = z - d * Math.atan(e);
            }
        }
        double[] ret = {x, y, z};
        return ret;
    }

    public static double asin(double A) {
        return loop(1, 0, A, 2, N)[2];
    }

    public static double cos(double theta) {
        theta = Math.abs(theta);
        if (theta > Math.PI/2) {
            int n = (int) Math.floor(theta/(2*Math.PI));
            theta -= n*(2*Math.PI);
            System.out.println(theta);
            if (theta > Math.PI/2) {
                theta -= Math.PI/2;
                System.out.println(theta);
                return - loop(1, 0, theta, 1, N)[1];
            }
        }
        return loop(1, 0, theta, 1, N)[0];
    }

    public static double sin(double theta) {
        double halfpi = Math.PI/2;
        if (theta > Math.PI/2) {
            int e = (int) Math.floor(theta/(2*Math.PI));
            System.out.println(e);
            theta -= e*Math.PI*2;
            theta -= halfpi;
            System.out.println(theta);
        }
        return loop(1, 0, theta, 1, N)[1];
    }

    public static double arctan(double A) {
        return loop(1, A, 0, 0, N)[2];
    }

    public static void main(String[] args) {
        System.out.println("fonction\tcordic\tjava\terreur");

        for (String arg : args) {
            double A = Double.parseDouble(arg);

            double atanC = arctan(A);
            double atanJ = Math.atan(A);
            double erreur = Math.abs(atanC - atanJ);
            System.out.println("arctan("+ arg +")\t"+ Double.toString(atanC) +"\t"+ Double.toString(atanJ) + "\t" + Double.toString(erreur));

            double sinC = sin(A);
            double sinJ = Math.sin(A);
            erreur = Math.abs(sinC - sinJ);
            System.out.println("sin("+ arg +")\t"+ Double.toString(sinC) +"\t"+ Double.toString(sinJ) + "\t" + Double.toString(erreur));

            double cosC = cos(A);
            double cosJ = Math.cos(A);
            erreur = Math.abs(cosC - cosJ);
            System.out.println("cos("+ arg +")\t"+ Double.toString(cosC) +"\t"+ Double.toString(cosJ) + "\t" + Double.toString(erreur));

            System.out.println("asin("+arg+")\t"+ Double.toString(asin(A)) +"\t"+ Double.toString(Math.asin(A)));
        }

    }
}