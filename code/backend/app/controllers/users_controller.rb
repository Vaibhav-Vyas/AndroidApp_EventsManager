class UsersController < ApplicationController

	def sign_up
		user = User.new
		user.email = params[:email]
		user.password = params[:password]

		if User.find_by(email: params[:email])
			respond_to do |format|
				format.html {render plain: "User already exists" }
				format.json {render json: { status: 400, msg: "User already exists" } }
			end
		elsif user.save
			respond_to do |format|
				format.html {render plain: "User registered" }
				format.json {render json: { status: 200, msg: "User registered" } }
			end
		else
			respond_to do |format|
      	format.html {render plain: "Email or password is blank" }
				format.json {render json: { status: 400, msg: "Email or password is blank"} }
    	end
		end
	end

	def sign_in
		if User.where("email = ? AND password = ?", 	params[:email], params[:password]).blank?
			respond_to do |format|
				format.html {render plain: "Username or password incorrect" }
				format.json {render json: { status: 400, msg: "Username or password incorrect" } }
			end
		else
			respond_to do |format|
				format.html {render plain: "Signed in" }
				format.json {render json: { status: 200, msg: "Signed in" } }
			end
		end
	end
end
