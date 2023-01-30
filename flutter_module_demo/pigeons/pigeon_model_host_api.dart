import 'package:pigeon/pigeon.dart';

class Person {
  String? name;
  int? age;
}

// Communicate from flutter to android/ios
@HostApi()
abstract class PigeonModelApi {
  void activateSubmissionButton(bool enabled);
  Person getPerson();
}

