let {
    PythonShell
} = require('python-shell')

let predict = new PythonShell('predict.py');

sample = [23, 0, 420]

// sends a message to the Python script via stdin
predict.send('23, 0, 420\n23, 1, 120');

predict.on('message', function (message) {
    // received a message sent from the Python script (a simple "print" statement)
    console.log(message);
});

// end the input stream and allow the process to exit
predict.end(function (err, code, signal) {
    if (err) throw err;
    console.log('The exit code was: ' + code);
    console.log('The exit signal was: ' + signal);
    console.log('finished');
});
