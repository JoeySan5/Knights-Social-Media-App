// import 'package:file_picker/file_picker.dart';
// import 'package:flutter/material.dart';
// import 'dart:io';

// class FilepickerScreen extends StatefulWidget {
//   const FilepickerScreen({super.key});

//   @override
//   State<FilepickerScreen> createState() => _FilepickerScreenState();
// }

// class _FilepickerScreenState extends State<FilepickerScreen> {
//   // Variable for storing the selected single file
//   File? _file;

//   // Variable for storing selected multiple files
//   List<File?> _files = [];

//   // File picker implementation for a single file
//   getFile() async {
//     FilePickerResult? result = await FilePicker.platform.pickFiles();

//     if (result != null) {
//       final file = File(result.files.single.path!);
//       _file = file;
//       setState(() {});
//     } else {
//       // User canceled the picker
//       // You can show snackbar or fluttertoast
//       // here like this to show warning to user
//       // ignore: use_build_context_synchronously
//       ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
//         content: Text('Please select file'),
//       ));
//     }
//   }

//   // File picker implementation for a single file
//   getMultipleFile() async {
//     FilePickerResult? result = await FilePicker.platform.pickFiles(
//       allowMultiple: true,
//       type: FileType.any,
//     );

//     if (result != null) {
//       List<File?> files = result.paths.map((path) => File(path!)).toList();
//       _files = files;
//       setState(() {});
//     } else {
//       // User canceled the picker and didn't
//       // select atleast 1 file from device
//       // You can show snackbar or fluttertoast
//       // here like this to show warning to user
//       ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
//         content: Text('Please select atleast 1 file'),
//       ));
//     }
//   }

//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//         body: Center(
//           child: Column(
//             mainAxisAlignment: MainAxisAlignment.center,
//             children: [
//               // Widget to display selected file info
//               Column(
//                 mainAxisAlignment: MainAxisAlignment.center,
//                 crossAxisAlignment: CrossAxisAlignment.center,
//                 children: [
//                   Row(
//                     mainAxisAlignment: MainAxisAlignment.center,
//                     crossAxisAlignment: CrossAxisAlignment.center,
//                     children: [
//                       Text(
//                         _file != null ? "File Name:- " : "No file is Selected",
//                         textAlign: TextAlign.center,
//                         style: const TextStyle(
//                             color: Colors.black, fontWeight: FontWeight.bold),
//                       ),
//                     ],
//                   ),
//                   Row(
//                     mainAxisAlignment: MainAxisAlignment.center,
//                     crossAxisAlignment: CrossAxisAlignment.center,
//                     children: [
//                       Text(_file != null ? _file!.path.split("/").last : "",
//                           // To show name of file selected
//                           textAlign: TextAlign.center,
//                           style: const TextStyle(
//                             color: Colors.black,
//                           )),
//                     ],
//                   )
//                 ],
//               )
//               // Widget to display selected multiple files info
//               Expanded(
//                 child:
//                   ListView.builder(
//                     itemCount: files.length,
//                     itemBuilder: (BuildContext context, int index) {
//                       return ListTile(
//                       title: Text( files[index]!.path.split("/").last ,
//                         // To show name of files selected 
//                         style: const TextStyle(color: Colors.black)),
//                       );
//                     },
//                     )
//                 )
//             ],
//           ),
//         ),
//         floatingActionButtonLocation: FloatingActionButtonLocation.centerFloat,
//         floatingActionButton: FloatingActionButton(
//           onPressed: () {
//             getFile(); // Calling getFile() when the button is pressed
//           },
//           label: const Text("Select File"),
//         ));
//   }
// }
