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
        int b; // un byte llegit de l'arxiu (guardat dins d'un enter)
        
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
                System.out.println("L'arxiu no és un arxiu d'imatge JPG");
                try
                {
                    fis.close();
                }
                catch (IOException ex1)
                {
                    System.out.println("Ha passat un error tancant l'arxiu: " + ex1);
                    return;
                }
                return;
            }
        }
        catch (IOException ex)
        {
            System.out.println("Ha passat un error llegint l'arxiu: " + ex);
            try
            {
                fis.close();
            }
            catch (IOException ex1)
            {
                System.out.println("Ha passat un error tancant l'arxiu: " + ex1);
                return;
            }
            return;
        }

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