package Stock_Predictor;

import java.io.*;

public class ObjectManager {


    Object objectInputStream(File file, Object object){
        Object serializable = null;
        try(FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)){

            if(file.exists()){
                serializable = objectInputStream.readObject();
                return  serializable;
            }
            else{
                objectOutputStream(file,object);
                return objectInputStream(file,object);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    void objectOutputStream(File file,Object object){

        try(FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream =new ObjectOutputStream(fileOutputStream)){

            objectOutputStream.writeObject(object);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
