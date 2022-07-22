const {app} = require('./app');
const PORT = process.env.PORT || 8000;

var server = app.listen(PORT, () => {
    console.log("Application starts. http://" + server.address().address + ":" + server.address().port);
});
