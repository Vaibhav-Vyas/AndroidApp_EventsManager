class RemovePublicFromEvents < ActiveRecord::Migration
  def change
    remove_column :events, :public, :boolean
  end
end
