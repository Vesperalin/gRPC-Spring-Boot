package pl.edu.pwr.rsi.grpc.client;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@AllArgsConstructor
class Controller {
    FoobarService client;

    // mapping for test method
    // curl http://localhost:8080/sayHello/test
    @GetMapping("/sayHello/{name}")
    @ResponseBody
    public String sayHello(@PathVariable String name) {
        return client.sayHello(name);
    }

    // mapping for addStudent method from client
    // curl http://localhost:8080/addStudent/Anna,Nowak,111111,Chemistry
    // curl http://localhost:8080/addStudent/Amy,Smith,123123,Applied%20Computer%20Science
    @GetMapping("/addStudent/{name},{surname},{indexNumber},{fieldOfStudy}")
    @ResponseBody
    public String addStudent(@PathVariable String name, @PathVariable String surname, @PathVariable int indexNumber, @PathVariable String fieldOfStudy) {
        return client.addStudent(name, surname, indexNumber, fieldOfStudy);
    }

    // mapping for getStudent method from client
    // curl http://localhost:8080/getStudent/111111
    // curl http://localhost:8080/getStudent/1231
    @GetMapping("/getStudent/{indexNumber}")
    @ResponseBody
    public MyStudent getStudent(@PathVariable int indexNumber) {
        return client.getStudent(indexNumber);
    }

    // mapping for amountOfStudentsByFieldOfStudy method from client
    // curl http://localhost:8080/amountOfStudentsByFieldOfStudy/Chemistry
    @GetMapping("/amountOfStudentsByFieldOfStudy/{fieldOfStudy}")
    @ResponseBody
    public int amountOfStudentsByFieldOfStudy(@PathVariable String fieldOfStudy) {
        return client.amountOfStudentsByFieldOfStudy(fieldOfStudy);
    }

    // mapping for streamGetStudents method from client
    // curl http://localhost:8080/streamGetStudents
    @GetMapping("/streamGetStudents")
    @ResponseBody
    public ArrayList<MyStudent> streamGetStudents() {
        return client.streamGetStudents();
    }

    // mapping for uploadImage method from client
    // curl http://localhost:8080/uploadImage/obraz.jpg
    @GetMapping("/uploadImage/{path}")
    @ResponseBody
    public String uploadImage(@PathVariable String path) {
        return client.uploadImage(path);
    }

    // mapping for readImage method from client
    // curl http://localhost:8080/getImage
    @GetMapping("/getImage")
    @ResponseBody
    public String readImage() {
        return client.getImage();
    }
}
