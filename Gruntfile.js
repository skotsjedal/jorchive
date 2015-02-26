'use strict';

module.exports = function (grunt) {

    // Load grunt tasks automatically
    require('load-grunt-tasks')(grunt);
    require('time-grunt')(grunt);

    grunt.registerTask('install', 'install the backend and frontend dependencies', function() {
        var exec = require('child_process').exec;
        var cb = this.async();
        exec('bower install', {cwd: '.'}, function(err, stdout, stderr) {
            console.log(stdout);
            console.log(stderr);
            cb();
        });
    });

    // init required configurations for each task.
    grunt.initConfig({

            // Project settings
            config: {
                path: {
                    webapp: {
                        root: 'src/main/webapp',
                        html: 'src/main/webapp/WEB-INF/views'
                    }
                }
            },

            // From grunt-wiredep. Automatically inject Bower components into the HTML file
            wiredep: {
                target: {
                    src: '<%= config.path.webapp.html %>/index.html',
                    ignorePath: '../..'
                }
            }
        }
    );

    grunt.registerTask('build', [
        'install',
        'wiredep'
    ]);

    grunt.registerTask('default', [
        'build'
    ]);
};
