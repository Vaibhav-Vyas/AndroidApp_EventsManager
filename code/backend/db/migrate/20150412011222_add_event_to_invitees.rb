class AddEventToInvitees < ActiveRecord::Migration
  def change
    add_reference :invitees, :event, index: true, foreign_key: true
  end
end
