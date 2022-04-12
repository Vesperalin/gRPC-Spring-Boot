package pl.edu.pwr.rsi.grpc.client;

import com.google.protobuf.ByteString;
import pl.edu.pwr.rsi.grpc.interfaces.lib.*;
import pl.edu.pwr.rsi.grpc.interfaces.lib.MyServiceGrpc.MyServiceBlockingStub;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

// client  implementation
@Service
public class FoobarService {

    // stub (namiastka) - to communicate with server
    @GrpcClient("myService")
    private MyServiceBlockingStub stub;

    // implementation of calling test method
    public String sayHello(String name) {
        SimpleRequest request = SimpleRequest.newBuilder().setName(name).build();
        return stub.sayHello(request).getMessage();
    }

    // implementation of calling method that adds student - synchronous request
    public String addStudent(String name, String surname, int indexNumber, String fieldOfStudy) {
        pl.edu.pwr.rsi.grpc.interfaces.lib.Student request = pl.edu.pwr.rsi.grpc.interfaces.lib.Student.newBuilder().setName(name).setSurname(surname).setIndexNumber(indexNumber).setFieldOfStudy(fieldOfStudy).build();
        return stub.addStudent(request).getMessage();
    }

    // implementation of calling method that gets student by index number - synchronous request
    public MyStudent getStudent(int indexNumber) {
        StudentIndex request = StudentIndex.newBuilder().setIndexNumber(indexNumber).build();
        pl.edu.pwr.rsi.grpc.interfaces.lib.Student student = stub.getStudent(request);
        return new MyStudent(student.getName(), student.getSurname(), student.getIndexNumber(), student.getFieldOfStudy());
    }

    // implementation of calling method that gets amount of students by field of study - asynchronous request
    public int amountOfStudentsByFieldOfStudy(String fieldOfStudy) {
        System.out.println("Calling asynchronous method");
        int response = stub.amountOfStudentsByFieldOfStudy(FieldOfStudy.newBuilder().setFieldOfStudy(fieldOfStudy).build()).getAmount();
        System.out.println("Got results from asynchronous method");
        return response;
    }

    // implementation of calling method that reads students as stream
    public ArrayList<MyStudent> streamGetStudents() {
        pl.edu.pwr.rsi.grpc.interfaces.lib.Empty request = pl.edu.pwr.rsi.grpc.interfaces.lib.Empty.newBuilder().build();
        int recordsCount = stub.recordsCount(request).getCount();
        Iterator<Student> readRecords;
        ArrayList<MyStudent> students = new ArrayList<>();

        try {
            int readRecordsCount = 0, readRecordsChunk = 0, printed = 0;
            readRecords = stub.streamGetStudents(request);

            while (readRecords.hasNext()) {
                readRecordsCount++;
                readRecordsChunk++;

                for (int i = 1; readRecordsCount <= recordsCount && (double) i / 10.0 <= (double) readRecordsChunk / (double) recordsCount; i++) {
                    System.out.print("*");
                    printed++;
                    readRecordsChunk = 1;
                }

                Student reply = readRecords.next();
                students.add(new MyStudent(reply.getName(), reply.getSurname(), reply.getIndexNumber(), reply.getFieldOfStudy()));
            }
            for (int i = printed; i < 10; i++) {
                System.out.print("*");
            }
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return students;
    }

    // implementation of calling method that uploads image on server
    public String uploadImage(String path) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            byte[] chunk = new byte[4096];
            int bytesRead;
            InputStream stream = new FileInputStream(new File(path));
            while ((bytesRead = stream.read(chunk)) > 0) {
                outputStream.write(chunk, 0, bytesRead);
            }
        } catch (Exception e) {
            return "Can't find the image. Make sure you give correct image name";
        }
        UploadImageRequest request = UploadImageRequest.newBuilder().setImage(ByteString.copyFrom(outputStream.toByteArray())).build();
        return stub.uploadImage(request).getMessage();
    }

    // implementation of calling method that reads image from server
    public String getImage() {
        pl.edu.pwr.rsi.grpc.interfaces.lib.Empty request = pl.edu.pwr.rsi.grpc.interfaces.lib.Empty.newBuilder().build();
        ReadImageReply reply = stub.getImage(request);
        String result = reply.getMessage();
        if (!result.equals("No image")) {
            JFrame frame = new JFrame();
            frame.setSize(800, 500);
            JLabel label = new JLabel(new ImageIcon(reply.getImage().toByteArray()));
            frame.add(label);
            frame.setVisible(true);
        }
        return result;
    }

}
