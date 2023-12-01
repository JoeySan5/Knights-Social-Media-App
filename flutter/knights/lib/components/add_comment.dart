// import 'dart:io';

// import 'package:file_picker/file_picker.dart';
// import 'package:flutter/material.dart';

// import 'package:knights/net/web_requests.dart';

// import 'dart:convert';
// import 'dart:typed_data';

// class AddComment extends StatefulWidget {
//   final String sessionKey;
//   const AddComment({super.key, required this.sessionKey});

//   @override
//   State<AddComment> createState() => _AddComment();
// }

// class _AddComment extends State<AddComment> {
//   ///text controller used to retreive current value of the TextField
//   final myController = TextEditingController();
//   final GlobalKey<FormState> _formKey = GlobalKey<FormState>();

//   @override
//   void dispose() {
//     //clean up controller when the widget is disposed
//     myController.dispose();
//     super.dispose();
//   }

//   File? _selectedFile;

//   // Future<void> pickFile() async {
//   //   FilePickerResult? result = await FilePicker.platform.pickFiles();

//   //   if (result != null) {
//   //     setState(() {
//   //       var logoBase64 = result!.files.first.bytes;
//   //       //_selectedFile = File(result.files.single.path!);
//   //     });
//   //   } else {
//   //     // User canceled the picker
//   //     ScaffoldMessenger.of(context).showSnackBar(
//   //       const SnackBar(content: Text('No file selected')),
//   //     );
//   //   }
//   // }

//   Future<void> pickFile() async {
//     FilePickerResult? result = await FilePicker.platform.pickFiles();

//     if (result != null) {
//       setState(() {
//         _selectedFile = File(result.files.single.path!);
//         String fileName = result.files.first.name;
//         print('selected fileName: $fileName');
//         Uint8List? fileBytes = result.files.first.bytes;
//         String base64 = base64Encode(fileBytes!);
//         print('base64 file bytes: $base64');
//       });
//     } else {
//       // User canceled the picker
//       ScaffoldMessenger.of(context).showSnackBar(
//         const SnackBar(content: Text('No file selected')),
//       );
//     }
//   }

//   @override
//   Widget build(BuildContext context) {
//     String content = "";
//     String link = "";
//     return Form(
//       key: _formKey,
//       child: Column(
//         children: [
//           Padding(
//               padding: const EdgeInsets.all(8.0),
//               child: TextFormField(
//                 maxLines: 5, // reduced max lines
//                 decoration: const InputDecoration(
//                     border: OutlineInputBorder(),
//                     hintText: 'Please write your comment here...',
//                     labelText: 'Please write your comment here...',
//                     labelStyle: TextStyle(
//                         color: Colors.white,
//                         fontFamily: 'roboto',
//                         fontSize: 20),
//                     hintStyle: TextStyle(color: Colors.white),
//                     contentPadding: EdgeInsets.all(10)),
//                 onSaved: (String? value) {
//                   // Save the data when the form is saved
//                   if (value != null) {
//                     content = value;
//                   } else {
//                     content = "";
//                   }
//                   // The key parameter should be unique for each TextFormField
//                   print('content: $value');
//                 },
//                 style: const TextStyle(color: Colors.white),
//               )
//               ),
//           Padding(
//               padding: const EdgeInsets.all(8.0),
//               child: TextFormField(
//                 maxLines: 5, // reduced max lines
//                 decoration: const InputDecoration(
//                     border: OutlineInputBorder(),
//                     hintText: 'Please write your link here...',
//                     labelText: 'Please write your link here...',
//                     labelStyle: TextStyle(
//                         color: Colors.white,
//                         fontFamily: 'roboto',
//                         fontSize: 20),
//                     hintStyle: TextStyle(color: Colors.white),
//                     contentPadding: EdgeInsets.all(10)),
//                 onSaved: (String? value) {
//                   // Save the data when the form is saved
//                   if (value != null) {
//                     link = value;
//                   } else {
//                     link = "";
//                   }
//                   // The key parameter should be unique for each TextFormField
//                   print('content: $value');
//                 },
//                 style: const TextStyle(color: Colors.white),
//               )
//               //)
//               ),

//           // Attach file button
//           ElevatedButton(
//             onPressed: pickFile,
//             child: const Text('Attach File'),
//           ),

//           // Optional: Display the selected file name
//           if (_selectedFile != null)
//             Padding(
//               padding: const EdgeInsets.all(8.0),
//               child:
//                   Text("Selected File: ${_selectedFile!.path.split('/').last}"),
//             ),

//           // Submit button
//           ElevatedButton(
//               style: const ButtonStyle(
//                 backgroundColor: MaterialStatePropertyAll(Colors.green),
//               ),
//               //the arrow function ()=> allows for postIdeas to return a future
//               //this is done because onPressed accepts only voids, not future
//               onPressed: () => {
//                     if (_formKey.currentState!.validate())
//                       {
//                         _formKey.currentState!.save(),
//                         print('Form saved!'),

//                         /// update user profile if information is valid
//                         postIdeas(content, link, widget.sessionKey),
//                         print('posted comment sucessfully!'),
//                       },
//                     Navigator.pop(context)
//                   },
//               child: const Text('Submit')),
//         ],
//       ),
//     );
//   }
// }
