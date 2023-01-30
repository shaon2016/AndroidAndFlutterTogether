import 'package:flutter/material.dart';
import 'package:flutter_module_demo/pigeon_model_flutter_api.dart';
import 'package:flutter_module_demo/pigeon_model_host_api.dart';

@pragma("vm:entry-point")
void nativeLoad() async {
  WidgetsFlutterBinding.ensureInitialized();
  PigeonModelFlutter();
  runApp(const MyApp());
}

// To update flutter view when receive data from android/ios side
ValueNotifier<Employee?> notifier = ValueNotifier(null);

// Implementing flutter api
class PigeonModelFlutter implements PigeonFlutterApi {
  PigeonModelFlutter() {
    PigeonFlutterApi.setup(this);
  }

  // receiving employee data from native side
  @override
  void setEmployee(Employee emp) {
    notifier.value = emp;
  }
}

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: Scaffold(
        body: Home(),
      ),
    );
  }
}

class Home extends StatefulWidget {
  const Home({Key? key}) : super(key: key);

  @override
  State<Home> createState() => _HomeState();
}

class _HomeState extends State<Home> {
  PigeonModelApi modelApi = PigeonModelApi();
  int count = 0;
  Person? p;

  @override
  void initState() {
    super.initState();

    getPerson();
  }

  @override
  Widget build(BuildContext context) {
    Future<void> showMyDialog() async {
      return showDialog<void>(
        context: context,
        barrierDismissible: false, // user must tap button!
        builder: (BuildContext context) {
          return AlertDialog(
            title: const Text('AlertDialog Title'),
            content: SingleChildScrollView(
              child: ListBody(
                children: const <Widget>[
                  Text('This is a demo alert dialog.'),
                  Text('Would you like to approve of this message?'),
                ],
              ),
            ),
            actions: <Widget>[
              TextButton(
                child: const Text('Approve'),
                onPressed: () {
                  modelApi.activateSubmissionButton(true);

                  setState(() {
                    count++;
                  });
                  Navigator.of(context).pop();
                },
              ),
            ],
          );
        },
      );
    }

    return Center(
      child: Column(
        children: [
          ValueListenableBuilder(
              valueListenable: notifier,
              builder: (ctx, emp, _) {
                return Text("Employee Name ${emp?.name ?? "No Name"}");
              }),
          Text("Person Name ${p?.name ?? "No Name"}"),
          const SizedBox(
            height: 30,
          ),
          Text("Count $count"),
          const CircleAvatar(
            backgroundImage: AssetImage('assets/images/1.jpg'),
            radius: 100,
          ),
          const SizedBox(
            height: 30,
          ),
          ElevatedButton(
            onPressed: () {
              setState(() {
                count++;
              });
              showMyDialog();
            },
            child: const Text("Show Dialog"),
          ),
        ],
      ),
    );
  }

  void getPerson() async {
    final person = await modelApi.getPerson();
    setState(() {
      p = person;
    });
  }
}
