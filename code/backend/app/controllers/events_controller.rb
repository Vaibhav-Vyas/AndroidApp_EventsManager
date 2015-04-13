class EventsController < ApplicationController
	before_action :authenticate_user

	def create
		event = Event.new
		event.name = params[:name]
		event.location = params[:location]
		event.description = params[:description]
		event.type = params[:type]
		event.startTime = params[:startTime]
		event.endTime =  params[:endTime]
		event.imageUrl = params[:imageUrl]
		event.publicEvent = params[:publicEvent]
		event.organization = params[:organization]
	
		if event.save
			respond_to do |format|
				format.html {render plain: "Event created. Id = #{event.id}" }
				format.json {render json: { status: 200, eventId: event.id, msg: "Event created" } }
			end
		elsif
			respond_to do |format|
				format.html {render plain: "Error in creating event" }
				format.json {render json: { status: 400, msg: "Error in creating event" } }
			end
		end
	end

	def invite
		list = params[:list]
		eventId = params[:eventId]
		list.each do |x|
			invitee = Invitee.new
			invitee.email = x
			invitee.status = "invited"
			invitee.save
		end
		respond_to do |format|
			format.html {render plain: "Invited users" }
			format.json {render json: { status: 200, msg: "Invited users" } }
		end
	end

	private	
	def authenticate_user
		if !User.find_by(email: params[:email])
			respond_to do |format|
				format.html {render plain: "User doesn't exists" }
				format.json {render json: { status: 400, msg: "User doesn't exists" } }
			end
		end
	end
end
