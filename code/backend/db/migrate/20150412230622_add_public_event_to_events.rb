class AddPublicEventToEvents < ActiveRecord::Migration
  def change
    add_column :events, :publicEvent, :boolean
  end
end
