import 'dart:io';

import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';

class MultipleFilepickerScreen extends StatefulWidget {
  const MultipleFilepickerScreen({super.key});

  @override
  State<MultipleFilepickerScreen> createState() =>
      _MultipleFilepickerScreenState();
}

class _MultipleFilepickerScreenState extends State<MultipleFilepickerScreen> {
  getMultipleFile() async {
    FilePickerResult? result = await FilePicker.platform.pickFiles(
      allowMultiple: true,
      // You want to pass different argument
      // in these 2 property in above function
    );
    // 1.File type
    // Expected arguments you can pass
    // FileType.image // For selecting images only
    // FileType.audio // For selecting file type related to audio

    // FileType.media // For selecting file type related to any media include image,audio,video
    // FileType.video // For selecting file type related to video
    // FileType.custom // For selecting file type related to any custom file type which is not available in this list
    // FileType.any // For selecting any type of file

    // 2.allowedExtensions
    // In this you pass a list of extensions you want to allowed to be selected from device
    // Different extensions like png,pdf,jpg,jpeg,apk ,cls and many more

    if (result != null) {
      List<File?> file = result.paths.map((path) => File(path!)).toList();
      files = file;
      setState(() {});
    } else {
      // User canceled the picker and didn't select atleast 1 file from device

      // You can show snackbar or fluttertoast here like this to show warning to user
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
        content: Text('Please select atleast 1 file'),
      ));
    }
  }

// Variable for showing multiple file
  List<File?> files = [];
  @override
  Widget build(BuildContext context) {
    print(files);
    return Scaffold(
        body: ListView.builder(
          itemCount: files.length,
          itemBuilder: (BuildContext context, int index) {
            return ListTile(
              title: Text(files[index]!.path.split("/").last,
                  style: TextStyle(color: Colors.black)),
            );
          },
        ),
        floatingActionButtonLocation: FloatingActionButtonLocation.centerFloat,
        floatingActionButton: FloatingActionButton.extended(
          onPressed: () {
            // Function for
            // selecting multiple files
            getMultipleFile();
          },
          label: const Text("Select File"),
        ));
  }
}
