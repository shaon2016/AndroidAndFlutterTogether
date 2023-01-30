import 'package:pigeon/pigeon.dart';

class Employee {
  String? name;
}

@FlutterApi()
abstract class PigeonFlutterApi {
  void setEmployee(Employee emp);
}