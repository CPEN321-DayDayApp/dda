let {
    PythonShell
} = require('python-shell')

let retrain = new PythonShell('retrain.py');

retrain.send('23, 0, 420, 2\n23, 1, 120, 5');

retrain.on('message', function (message) {
    console.log(message);
});

retrain.end(function (err, code, signal) {
    if (err) throw err;
    console.log('The exit code was: ' + code);
    console.log('The exit signal was: ' + signal);
    console.log('finished');
});