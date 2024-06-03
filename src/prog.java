import java.util.Scanner;
import java.util.InputMismatchException;

public class prog {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = 0;
        double[][] a = null;
        double[] b = null;
        double tol = 0.0;

        // Matris a'nın girişini al
        while (a == null) {
            try {
                System.out.print("Matris a'nın satır ve sütun sayısını giriniz (kare matris): ");
                n = scanner.nextInt();
                if (n <= 0) {
                    throw new InputMismatchException("Boyut pozitif bir tam sayı olmalıdır.");
                }
                a = new double[n][n];
                System.out.println("Matrisin elemanlarını satır satır giriniz:");
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        while (true) {
                            try {
                                a[i][j] = scanner.nextDouble();
                                break;
                            } catch (InputMismatchException e) {
                                System.out.println("Hatalı giriş: Lütfen geçerli bir sayı giriniz.");
                                scanner.next(); // Hatalı girişi temizle
                            }
                        }
                    }
                }
            } catch (InputMismatchException e) {
                System.out.println("Hatalı giriş: Lütfen geçerli bir sayı giriniz.");
                scanner.next(); // Hatalı girişi temizle
                a = null; // Tekrar baştan başlaması için a'yı null yap
            }
        }

        // Vektör b'nin girişini al
        while (b == null) {
            try {
                b = new double[n];
                System.out.println("Vektör b'nin elemanlarını giriniz:");
                for (int i = 0; i < n; i++) {
                    while (true) {
                        try {
                            b[i] = scanner.nextDouble();
                            break;
                        } catch (InputMismatchException e) {
                            System.out.println("Hatalı giriş: Lütfen geçerli bir sayı giriniz.");
                            scanner.next();
                        }
                    }
                }
            } catch (InputMismatchException e) {
                System.out.println("Hatalı giriş: Lütfen geçerli bir sayı giriniz.");
                scanner.next();
                b = null;
            }
        }

        // Tolerans değerini al
        while (tol == 0.0) {
            try {
                System.out.print("Tolerans değerini giriniz: ");
                tol = scanner.nextDouble();
                if (tol <= 0) {
                    throw new InputMismatchException("Tolerans pozitif bir sayı olmalıdır.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Hatalı giriş: Lütfen geçerli bir sayı giriniz.");
                scanner.next();
            }
        }

        double[] x = new double[b.length];
        double[] er = new double[1]; // Hata kodu
        ludecomp(a, b, b.length, tol, x, er);

        if (er[0] != -1) {
            System.out.println("\nÇözüm:");
            for (int i = 0; i < b.length; i++) {
                System.out.println((float)x[i]);
            }
        } else {
            System.out.println("Tolerans seviyesi nedeniyle çözüm bulunamadı.");
        }
    }

    // LU Ayrıştırma fonksiyonu
    public static void ludecomp(double[][] a, double[] b, int n, double tol, double[] x, double[] er) {
        double[] o = new double[n];
        double[] s = new double[n];
        decompose(a, n, tol, o, s, er);

        if (er[0] != -1) {
            substitute(a, o, n, b, x);
        }
    }

    // Ayrıştırma fonksiyonu
    public static void decompose(double[][] a, int n, double tol, double[] o, double[] s, double[] er) {
        for (int i = 0; i < n; i++) {
            o[i] = i;
            s[i] = Math.abs(a[i][0]);
            for (int j = 1; j < n; j++) {
                if (Math.abs(a[i][j]) > s[i]) {
                    s[i] = Math.abs(a[i][j]);
                }
            }
        }

        for (int k = 0; k < n - 1; k++) {
            pivot(a, o, s, n, k);
            int temp = (int) o[k];
            if (Math.abs(a[temp][k] / s[temp]) < tol) {
                er[0] = -1;
                return;
            }

            for (int i = k + 1; i < n; i++) {
                int temp2 = (int) o[i];
                double factor = a[temp2][k] / a[temp][k];
                a[temp2][k] = factor;
                for (int j = k + 1; j < n; j++) {
                    a[temp2][j] -= factor * a[temp][j];
                }
            }
        }

        int temp = (int) o[n - 1];
        if (Math.abs(a[temp][n - 1] / s[temp]) < tol) {
            er[0] = -1;
        }
    }

    // Yerine Koyma (Substitution) fonksiyonu
    public static void substitute(double[][] a, double[] o, int n, double[] b, double[] x) {
        double[] y = new double[n];

        // İleri Yerine Koyma (Forward Substitution)
        for (int i = 0; i < n; i++) {
            double sum = b[(int) o[i]];
            for (int j = 0; j < i; j++) {
                sum -= a[(int) o[i]][j] * y[j];
            }
            y[i] = sum;
        }

        // Geri Yerine Koyma (Back Substitution)
        x[n - 1] = y[n - 1] / a[(int) o[n - 1]][n - 1];
        for (int i = n - 2; i >= 0; i--) {
            double sum = y[i];
            for (int j = i + 1; j < n; j++) {
                sum -= a[(int) o[i]][j] * x[j];
            }
            x[i] = sum / a[(int) o[i]][i];
        }
    }

    // Pivotlama fonksiyonu
    public static void pivot(double[][] a, double[] o, double[] s, int n, int k) {
        int p = k;
        double big = Math.abs(a[(int) o[k]][k] / s[(int) o[k]]);

        for (int i = k + 1; i < n; i++) {
            double dummy = Math.abs(a[(int) o[i]][k] / s[(int) o[i]]);
            if (dummy > big) {
                big = dummy;
                p = i;
            }
        }

        double tempO = o[p];
        o[p] = o[k];
        o[k] = tempO;
    }
}
