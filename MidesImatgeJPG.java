import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

public class MidesImatgeJPG
{
    public static void main(String[] args)
    {
        File f; // per fer comprovacions de l'arxiu
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
            System.out.println("Errot: L'arxiu que ha indicat no existeix");
            return;
        }

        // comprovo que l'arxiu tingui extensió jpg/jpeg/JPG/JPEG
        //
        pos = args[0].lastIndexOf(".");
        if (pos == -1)
        {
            System.out.println("Error: L'arxiu que ha indicat no té extensió jpg/jpeg/JPG/JPEG");
            return;
        }
        extensio = args[0].substring(pos+1);
        if (!extensio.equals("jpg") && !extensio.equals("jpeg") &&
            !extensio.equals("JPG") && !extensio.equals("JPEG"))
        {
            System.out.println("Error: L'arxiu que ha indicat no té extensió jpg/jpeg/JPG/JPEG");
            return;
        }

        try (FileInputStream fis = new FileInputStream((args[0])))
        {        
            if (fis.read() != 0xFF || fis.read() != 0xD8)
            {
                throw new IOException("L'arxiu que ha indicat no és un arxiu JPEG vàlid (falta el marcador SOI).");
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
                    throw new IOException("Final inesperat de l'arxiu.");
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

                if (lenHi == -1 | lenLo == -1)
                {
                    throw new IOException("Final inesperat de l'arxiu.");
                }

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

                    if (heightHi == -1 | heightLo == -1)
                    {
                        throw new IOException("Final inesperat de l'arxiu.");
                    }

                    int widthHi = fis.read();
                    int widthLo = fis.read();
                    int width = (widthHi << 8) + widthLo;

                    if (widthHi == -1 | widthLo == -1)
                    {
                        throw new IOException("Final inesperat de l'arxiu.");
                    }

                    System.out.printf("Dimensions (ample x alt): %d x %d píxels%n", width, height);
                    break;
                }
                else
                {
                    //
                    // es tracta d'un altre tipus de segment que no ens interessa i per tant ens el saltem
                    // (resta 2 perquè la longitud del segment inclou els 2 bytes on s'especifica la longitud i ja els hem llegit)
                    //
                    if (fis.skip(segmentLength - 2) != segmentLength - 2)
                    {
                        throw new IOException("Final inesperat de l'arxiu.");
                    }
                }
            }
        }
        catch (IOException e) 
        {
            System.err.println("Error: " + e.getMessage());
        }

    }

}