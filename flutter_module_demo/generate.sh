flutter pub run pigeon \
  --input pigeons/pigeon_model_host_api.dart \
  --dart_out lib/pigeon_model_host_api.dart \
  --java_out ./.android/app/src/main/java/com/bkash/flutter_module_demo/host/PigeonModelHostApi.java \
  --java_package "com.bkash.flutter_module_demo.host"

flutter pub run pigeon \
  --input pigeons/pigeon_model_flutter_api.dart \
  --dart_out lib/pigeon_model_flutter_api.dart \
  --java_out ./.android/app/src/main/java/com/bkash/flutter_module_demo/host/PigeonModelFlutterApi.java \
  --java_package "com.bkash.flutter_module_demo.host"

