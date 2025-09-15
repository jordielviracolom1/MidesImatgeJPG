import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;

public class MidesImatgeJPG
{
    public static void main(String[] args)
    {
        File f; // per fer comprovacions de l'arxiu
        FileInputStream fis; // per accedir el contingut de l'arxiu
        int pos; // posició del darrer caràcter . en el nom de l'arxiu
        String extensio; // extensió de l'arxiu
        
        // comprovo que l'usuari hagi proporcionat un paràmetre
        //
        if (args.length != 1)
        {
            System.out.println("Per executar aquest programa cal indicar un paràmetre");
            System.out.println("amb el nom d'un arxiu d'imatge JPG");
            return;
        }

        // comprovo que el paràmetre sigui el nom d'un arxiu existent
        //
        f = new File(args[0]);
        if (!f.exists() || !f.isFile())
        {
            System.out.println("L'arxiu que ha indicat no existeix");
            return;
        }

        // comprovo que l'arxiu tingui extensió jpg/jpeg/JPG/JPEG
        //
        pos = args[0].lastIndexOf(".");
        if (pos == -1)
        {
            System.out.println("L'arxiu que ha indicat no té extensió jpg/jpeg/JPG/JPEG");
            return;
        }
        extensio = args[0].substring(pos+1);
        if (!extensio.equals("jpg") && !extensio.equals("jpeg") &&
            !extensio.equals("JPG") && !extensio.equals("JPEG"))
        {
            System.out.println("L'arxiu que ha indicat no té extensió jpg/jpeg/JPG/JPEG");
            return;
        }

        try
        {
            fis = new FileInputStream(args[0]);
        }
        catch (FileNotFoundException ex)
        {
            /*
             * no hauria de passar mai perquè ja he comprovat abans que existeix
             */
            System.out.println("L'arxiu que ha indicat no es pot accedir");
            return;
        }

        try
        {
            if (fis.read() != 0xFF || fis.read() != 0xD8)
            {
                throw new IOException("No és un fitxer JPEG vàlid (falta el marcador SOI).");
            }

            while (true)
            {
                int markerPrefix = fis.read();
                if (markerPrefix != 0xFF)
                {
                    throw new IOException("Error en el format: s’esperava marcador FF.");
                }

                int marker = fis.read();
                if (marker == -1)
                {
                    throw new IOException("Final inesperat de fitxer.");
                }

                // Segments que no porten longitud (com SOI, EOI, RSTx...)
                //
                if (marker == 0xD9)
                {   
                    //
                    // EOI End Of Image
                    //
                    throw new IOException("No s’ha trobat cap segment SOF.");
                }

                // Llegim longitud del segment
                //
                int lenHi = fis.read();
                int lenLo = fis.read();
                int segmentLength = (lenHi << 8) + lenLo;

                if (segmentLength < 2)
                {
                    throw new IOException("Longitud de segment invàlida.");
                }

                // Verifiquem si és un SOF (Start Of Frame)
                //
                if (marker == 0xC0 || marker == 0xC1 || marker == 0xC2)
                {
                    fis.read(); // precisió (normalment 8)
                    int heightHi = fis.read();
                    int heightLo = fis.read();
                    int height = (heightHi << 8) + heightLo;

                    int widthHi = fis.read();
                    int widthLo = fis.read();
                    int width = (widthHi << 8) + widthLo;

                    System.out.printf("Dimensions: %d x %d píxels%n", width, height);
                    break;
                }
                else
                {
                    // Saltar la resta del segment
                    fis.skip(segmentLength - 2);
                }
            }
        }
        catch (IOException e) 
        {
            System.err.println("Error: " + e.getMessage());
        }
        finally
        {
            tancarArxiu(fis);
        }

    }

    private static void tancarArxiu(FileInputStream fis)
    {
        try
        {
            fis.close();
        }
        catch (IOException ex)
        {
            System.out.println("Ha passat un error tancant l'arxiu: " + ex);
            return;
        }
    }
}