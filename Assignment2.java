// use this site to help you: https://planetcalc.com/8995/
// gonna need to do a primality test with this potentially...
// p = 7477593670943811622381204384069354428503282772648614748638717136928507379785934557170140876675222957340434428077365698677474428857398601288792965348470819
// q = 8880687721884226830351625505778821344847693517915244958759855480500072696419605246763466562391363703894702933585997990119250462415636703921724994688138409


import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Assignment2 {
    private static BigInteger p = new BigInteger("8EC5B76CFFC8F932F224CA5232BB9C2723314A2A5C06D97D13C74FA1BD3BD2CA43FF900BF818F13DA8FF45E78417A51EDB6BC0EA2A374610BB7EE1B070BE3023", 16);
    private static BigInteger q = new BigInteger("A98FE5B9DA80A53F4A65C31246BEB0B7F701920B9BC171B94CF87C551628B21A0A8B2F04F1882995C56B3FFF6453ADB825C1053D7C8E9195CEE77ED8B9183CA9", 16);
    private static BigInteger e = new BigInteger("65537");

    private static BigInteger calculateQuotient(BigInteger p, BigInteger q) {
        return (p.subtract(BigInteger.ONE)).multiply((q.subtract(BigInteger.ONE)));
    }

    public static BigInteger myEuclidean(BigInteger n, BigInteger e) {
        BigInteger x = new BigInteger("0");
        BigInteger y = new BigInteger("1");
        BigInteger lastX = new BigInteger("1");
        BigInteger lastY = new BigInteger("0");
        BigInteger temp;

        BigInteger next;
        BigInteger r;

        while (e.compareTo(BigInteger.ZERO) != 0) {
            next = n.divide(e);
            r = n.mod(e);

            n = e;
            e = r;

            temp = x;
            x = lastX.subtract(next.multiply(x));
            lastX = temp;

            temp = y;
            y = lastY.subtract(next.multiply(y));
            lastY = temp;
        }
        // System.out.println("Roots: " + lastX.toString() + ", " + lastY.toString());

        return lastY;
    }

    public static BigInteger myChineseTheorem(BigInteger m, BigInteger d, BigInteger p, BigInteger q) {
        // p = t1, q = t2
        BigInteger n = p.multiply(q);

        BigInteger a1 = m.modPow(d, p);
        BigInteger a2 = m.modPow(d, q);

        BigInteger N1 = q;
        BigInteger N2 = p;

        // need to make sure that both N numbers are less than their modulus
        BigInteger Y1 = myEuclidean(p, N1.mod(p)).mod(p);
        BigInteger Y2 = myEuclidean(q, N2.mod(q)).mod(q);

        BigInteger message = a1.multiply(N1).multiply(Y1);
        message = message.add(a2.multiply(N2).multiply(Y2));
        message = message.mod(n);

        return message;
    }

    public static BigInteger hashFile(String filename) throws IOException, NoSuchAlgorithmException {
        byte[] message = Files.readAllBytes(Path.of(filename));
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        byte[] hash = digest.digest(message);

        return new BigInteger(1, hash);

    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        BigInteger quotientN = calculateQuotient(p, q);

        BigInteger d = myEuclidean(quotientN, e);

        d = d.mod(quotientN);

        // System.out.println(hashFile("test.txt"));

        BigInteger messageDigest = hashFile(args[0]);

        System.out.println(myChineseTheorem(messageDigest, d, p, q).toString(16));

        // System.out.println(d.toString());
        // System.out.println(myChineseTheorem(new BigInteger("27"), new BigInteger("37"), new BigInteger("11"), new BigInteger("5")));
    }
}
