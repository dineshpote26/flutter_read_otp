import 'package:flutter/material.dart';
import 'package:read_otp_plugin/read_otp_plugin.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _textContent = 'Waiting for messages...';
  ReadOtpPlugin _smsReceiver;

  @override
  void initState() {
    super.initState();
    _smsReceiver = ReadOtpPlugin(onSmsReceived);
    _startListening();
  }

  void onSmsReceived(String message) {
    setState(() {
      _textContent = message;
    });
  }

  void onTimeout() {
    setState(() {
      _textContent = "Timeout!!!";
    });
  }

  void _startListening() {
    _smsReceiver.startListening(phoneNumberFilter: "7777");
    setState(() {
      _textContent = "Waiting for messages...";
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('SMS Listener App'),
        ),
        body: Column(
          children: <Widget>[
            Container(
              padding: const EdgeInsets.symmetric(vertical: 16.0),
              alignment: Alignment.center,
              child: Text(_textContent),
            ),
            RaisedButton(
              child: Text("Listen Again"),
              onPressed: _startListening,
            ),
          ],
        ),
      ),
    );
  }
}
