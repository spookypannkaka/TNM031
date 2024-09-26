from flask import Flask, session, request, jsonify, send_from_directory
from flask_cors import CORS

app = Flask(__name__, static_folder="../frontend/build", static_url_path='/')
app.secret_key = 'supersecretkey'

# Allow cross-origin requests with credentials from any origin
CORS(app, supports_credentials=True, resources={r"/*": {"origins": "*"}})
app.config['SESSION_COOKIE_SAMESITE'] = 'None'  # Allow cross-site cookie access
app.config['SESSION_COOKIE_SECURE'] = False  # Disable secure cookies for local testing

# Serve React frontend
@app.route('/')
@app.route('/<path:path>')  # Catch-all route for serving static files
def serve_react(path=''):
    if path == '' or path == 'index.html':
        return send_from_directory(app.static_folder, 'index.html')
    else:
        return send_from_directory(app.static_folder, path)

# Login and create a session
@app.route('/login', methods=['POST', 'GET'])
def login():
    data = request.json  # Get JSON data from frontend
    session['username'] = data.get('username')  # Store username in session
    return jsonify({"message": f"Welcome, {session['username']}!"})

# Expose session token
@app.route('/session-token')
def get_session_token():
    token = session.get('username')  # Retrieve session data (username)
    
    return jsonify({"session_token": token})

if __name__ == '__main__':
    app.run(debug=True, port=5000)
