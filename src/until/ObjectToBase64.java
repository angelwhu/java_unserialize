package until;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Base64;

public class ObjectToBase64 {
    public static String toBase64(Object object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(object);
        out.flush();
        byte[] userInfoBytes = bos.toByteArray();

        return Base64.getEncoder().encodeToString(userInfoBytes);
    }
}
